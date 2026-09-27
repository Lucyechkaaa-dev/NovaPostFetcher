package com.novapost.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.novapost.client.NovaPostClient;
import com.novapost.controller.NovaPostApiException;
import com.novapost.model.InternetDocumentListFilter;
import com.novapost.model.InternetDocumentListItem;
import com.novapost.projection.ModelProjector;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class WaybillFetcherService {

	private static final System.Logger LOGGER = System.getLogger(WaybillFetcherService.class.getName());
	public static final int PAGE_LIMIT = 100;
	public static final long DEFAULT_THROTTLE_DELAY_MS = 300;
	public static final int DEFAULT_MAX_RETRIES = 5;
	public static final long DEFAULT_INITIAL_BACKOFF_MS = 1000;

	private final NovaPostClient client;
	private final ModelProjector projector;
	private final long throttleDelayMs;
	private final int maxRetries;
	private final long initialBackoffMs;

	public WaybillFetcherService(NovaPostClient client) {
		this(client, ModelProjector.getInstance(), DEFAULT_THROTTLE_DELAY_MS, DEFAULT_MAX_RETRIES, DEFAULT_INITIAL_BACKOFF_MS);
	}

	public WaybillFetcherService(NovaPostClient client, long throttleDelayMs) {
		this(client, ModelProjector.getInstance(), throttleDelayMs, DEFAULT_MAX_RETRIES, DEFAULT_INITIAL_BACKOFF_MS);
	}

	public WaybillFetcherService(NovaPostClient client, ModelProjector projector, long throttleDelayMs, int maxRetries, long initialBackoffMs) {
		this.client = Objects.requireNonNull(client, "client must not be null");
		this.projector = projector != null ? projector : ModelProjector.getInstance();
		this.throttleDelayMs = Math.max(0, throttleDelayMs);
		this.maxRetries = Math.max(1, maxRetries);
		this.initialBackoffMs = Math.max(100, initialBackoffMs);
	}

	public List<InternetDocumentListItem> fetchAllDocuments(LocalDate from, LocalDate to) {
		return fetchAllDocuments(from, to, InternetDocumentListItem.class);
	}

	public <T> List<T> fetchAllDocuments(LocalDate from, LocalDate to, Class<T> targetType) {
		List<T> result = new ArrayList<>();
		fetchAllDocuments(from, to, targetType, result::addAll);
		return result;
	}

	public List<Map<String, Object>> fetchAllDocuments(LocalDate from, LocalDate to, Map<String, Object> targetFields) {
		List<Map<String, Object>> result = new ArrayList<>();
		fetchAllDocuments(from, to, targetFields, result::addAll);
		return result;
	}

	public void fetchAllDocuments(LocalDate from, LocalDate to, Consumer<List<InternetDocumentListItem>> batchConsumer) {
		fetchAllDocuments(from, to, InternetDocumentListItem.class, batchConsumer);
	}

	public <T> void fetchAllDocuments(LocalDate from, LocalDate to, Class<T> targetType, Consumer<List<T>> batchConsumer) {
		Objects.requireNonNull(targetType, "targetType must not be null");
		Objects.requireNonNull(batchConsumer, "batchConsumer must not be null");
		fetchAllBatchesRaw(from, to, rawArray -> {
			List<T> batch = projector.projectJsonArrayToClass(rawArray, targetType);
			if (!batch.isEmpty()) {
				batchConsumer.accept(batch);
			}
		});
	}

	public void fetchAllDocuments(LocalDate from, LocalDate to, Map<String, Object> targetFields, Consumer<List<Map<String, Object>>> batchConsumer) {
		Objects.requireNonNull(targetFields, "targetFields must not be null");
		Objects.requireNonNull(batchConsumer, "batchConsumer must not be null");
		fetchAllBatchesRaw(from, to, rawArray -> {
			List<Map<String, Object>> batch = projector.projectJsonArrayToMap(rawArray, targetFields);
			if (!batch.isEmpty()) {
				batchConsumer.accept(batch);
			}
		});
	}

	public void fetchAllBatchesRaw(LocalDate from, LocalDate to, Consumer<JsonNode> rawBatchConsumer) {
		validateDateRange(from, to);
		Objects.requireNonNull(rawBatchConsumer, "rawBatchConsumer must not be null");

		for (LocalDate currentDay = from; !currentDay.isAfter(to); currentDay = currentDay.plusDays(1)) {
			int page = 1;
			while (true) {
				JsonNode dataArray = fetchPageWithRetry(currentDay, page);

				if (dataArray == null || !dataArray.isArray() || dataArray.isEmpty()) {
					break;
				}

				rawBatchConsumer.accept(dataArray);

				if (dataArray.size() < PAGE_LIMIT) {
					break;
				}

				page++;
				applyThrottling();
			}
			applyThrottling();
		}
	}

	private JsonNode fetchPageWithRetry(LocalDate day, int page) {
		InternetDocumentListFilter filter = InternetDocumentListFilter.byDay(day, page, PAGE_LIMIT);

		long backoff = initialBackoffMs;
		for (int attempt = 1; attempt <= maxRetries; attempt++) {
			try {
				JsonNode root = client.executeForRootNode(NovaPostClient.MODEL_INTERNET_DOCUMENT, "getDocumentList", filter);

				if (root == null || root.isMissingNode()) {
					LOGGER.log(System.Logger.Level.WARNING, "Received empty JSON root for day {0} page {1}", day, page);
					return null;
				}

				boolean success = root.path("success").asBoolean(true);
				JsonNode errorsNode = root.path("errors");
				boolean hasErrors = !success || (errorsNode.isArray() && !errorsNode.isEmpty());

				if (hasErrors) {
					List<String> errors = new ArrayList<>();
					if (errorsNode.isArray()) {
						for (JsonNode err : errorsNode) {
							errors.add(err.asText());
						}
					}

					boolean isRateLimit = errors.stream().anyMatch(e -> e != null &&
							(e.toLowerCase().contains("to many requests") || e.toLowerCase().contains("too many requests")));

					if (isRateLimit && attempt < maxRetries) {
						LOGGER.log(System.Logger.Level.WARNING, "Rate limit hit for day {0} page {1}. Retrying in {2}ms (attempt {3}/{4})...",
								day, page, backoff, attempt, maxRetries);
						sleep(backoff);
						backoff *= 2;
						continue;
					}

					LOGGER.log(System.Logger.Level.ERROR, "Nova Post API error for date {0} page {1}: {2}", day, page, errors);
					System.err.println("Nova Post API error for date " + day + " page " + page + ": " + errors);
					throw new NovaPostApiException("Nova Post API error in getDocumentList for date " + day + " page " + page, errors);
				}

				return root.path("data");

			} catch (IOException | InterruptedException e) {
				if (e instanceof InterruptedException) {
					Thread.currentThread().interrupt();
					throw new RuntimeException("Thread interrupted while fetching waybills for date " + day + " page " + page, e);
				}

				if (attempt == maxRetries) {
					LOGGER.log(System.Logger.Level.ERROR, "Failed to fetch waybills for date {0} page {1} after {2} attempts: {3}",
							day, page, maxRetries, e.getMessage());
					throw new NovaPostApiException("Failed to fetch waybills for date " + day + " page " + page + " after " + maxRetries + " attempts: " + e.getMessage(), e);
				}

				LOGGER.log(System.Logger.Level.WARNING, "Transient error fetching day {0} page {1}: {2}. Retrying in {3}ms (attempt {4}/{5})...",
						day, page, e.getMessage(), backoff, attempt, maxRetries);
				sleep(backoff);
				backoff *= 2;
			}
		}

		throw new NovaPostApiException("Failed to fetch waybills for date " + day + " page " + page + " after " + maxRetries + " attempts");
	}

	private void applyThrottling() {
		if (throttleDelayMs > 0) {
			sleep(throttleDelayMs);
		}
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Thread interrupted during sleep", e);
		}
	}

	private static void validateDateRange(LocalDate from, LocalDate to) {
		if (from == null || to == null) {
			throw new IllegalArgumentException("from and to dates must not be null");
		}
		if (from.isAfter(to)) {
			throw new IllegalArgumentException("from date (" + from + ") must not be after to date (" + to + ")");
		}
	}

	public NovaPostClient getClient() {
		return client;
	}

	public ModelProjector getProjector() {
		return projector;
	}

	public long getThrottleDelayMs() {
		return throttleDelayMs;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public long getInitialBackoffMs() {
		return initialBackoffMs;
	}
}
