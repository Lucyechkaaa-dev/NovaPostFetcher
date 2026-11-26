package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Request of {@code InternetDocumentGeneral/getDocumentList}.
 * <p>{@code GetFullList} decides how the answer is delivered: {@code 1} returns the list for the period but
 * <b>at most 100 records</b> and needs no page; {@code 0} switches to paging without a limit on the total and requires
 * {@code Page}. Everything that takes a page therefore uses {@link #PAGED}; {@link #list(LocalDateTime, LocalDateTime)}
 * is the one-shot, capped variant.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InternetDocumentListFilter(
		@JsonProperty("DateTime") String dateTime,
		@JsonProperty("DateTimeFrom") String dateTimeFrom,
		@JsonProperty("DateTimeTo") String dateTimeTo,
		@JsonProperty("Page") Integer page,
		@JsonProperty("Limit") Integer limit,
		@JsonProperty("GetFullList") Integer getFullList,
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("RedeliveryMoney") Integer redeliveryMoney
){

	/** {@code GetFullList=0}: paged loading, {@code Page} required. */
	public static final int PAGED = 0;
	/** {@code GetFullList=1}: one list, capped by the API at 100 records. */
	public static final int LIST = 1;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	/** The shape before {@code IntDocNumber} and {@code RedeliveryMoney} were modelled. */
	public InternetDocumentListFilter(String dateTime, String dateTimeFrom, String dateTimeTo, Integer page, Integer limit, Integer getFullList){
		this(dateTime, dateTimeFrom, dateTimeTo, page, limit, getFullList, null, null);
	}

	/** One page of a paged load ({@code GetFullList=0}). */
	public static InternetDocumentListFilter byDateRange(LocalDateTime from, LocalDateTime to, int page, int limit){
		return byDateRange(from, to, page, limit, false);
	}

	/** @param getFullList {@code true} = capped one-shot list ({@code 1}), {@code false} = paged ({@code 0}) */
	public static InternetDocumentListFilter byDateRange(LocalDateTime from, LocalDateTime to, int page, int limit, boolean getFullList){
		return new InternetDocumentListFilter(null, date(from), date(to), page, limit, getFullList ? LIST : PAGED, null, null);
	}

	/** The capped (at most 100 records) list for the period, without paging. */
	public static InternetDocumentListFilter list(LocalDateTime from, LocalDateTime to){
		return new InternetDocumentListFilter(null, date(from), date(to), null, null, LIST, null, null);
	}

	/** One page of one calendar day. */
	public static InternetDocumentListFilter byDay(LocalDate day, int page, int limit){
		if(day == null){
			return byDateRange(null, null, page, limit);
		}
		String formatted = day.format(DATE_FORMATTER);
		return new InternetDocumentListFilter(null, formatted + " 00:00:00", formatted + " 23:59:59", page, limit, PAGED, null, null);
	}

	public static InternetDocumentListFilter of(String dateTimeFrom, String dateTimeTo, int page, int limit){
		return new InternetDocumentListFilter(null, dateTimeFrom, dateTimeTo, page, limit, PAGED, null, null);
	}

	/**
	 * One page of the waybills of a period narrowed to one TTN with the documented {@code IntDocNumber} filter. The result is
	 * still matched by number on the client, so a server that ignores the filter is handled by paging on.
	 */
	public static InternetDocumentListFilter byNumber(String ttn, LocalDateTime from, LocalDateTime to, int page, int limit){
		return new InternetDocumentListFilter(null, date(from), date(to), page, limit, PAGED, ttn, null);
	}

	/** Also returns the waybills with a backward delivery ("Грошовий переказ" / "Каса форпост"). */
	public InternetDocumentListFilter withRedeliveryMoney(){
		return new InternetDocumentListFilter(dateTime, dateTimeFrom, dateTimeTo, page, limit, getFullList, intDocNumber, 1);
	}

	private static String date(LocalDateTime value){
		return value != null ? value.format(DATE_FORMATTER) : null;
	}
}
