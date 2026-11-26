package com.novapost.model.support;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Tolerant parsing of the date formats Nova Poshta uses. The documentation and the live API disagree between
 * methods (and sometimes inside one method), for example {@code 25.11.2026}, {@code 25.11.2026 13:05:00},
 * {@code 2026-11-25 13:05:00}, {@code 25-11-2026 13:05:00}, {@code 2026-11-25 00:00:00.000000},
 * {@code 2015-03-20T13:45:19+00:00} (an offset is dropped: the local part is kept) or the US style {@code 4/2/21 0:00}
 * that two of the documentation's own examples show.
 */
public final class NpDates{

	private static final List<DateTimeFormatter> DATE_TIME = List.of(
			DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"),
			DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"),
			new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
					.optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).optionalEnd().toFormatter(),
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
			DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
			DateTimeFormatter.ISO_LOCAL_DATE_TIME,
			DateTimeFormatter.ofPattern("M/d/yy H:mm[:ss]")
	);

	private static final List<DateTimeFormatter> DATE = List.of(
			DateTimeFormatter.ofPattern("dd.MM.yyyy"),
			DateTimeFormatter.ofPattern("yyyy-MM-dd"),
			DateTimeFormatter.ofPattern("dd-MM-yyyy")
	);

	private static final List<DateTimeFormatter> TIME = List.of(
			DateTimeFormatter.ofPattern("H:mm"),
			DateTimeFormatter.ofPattern("H:mm:ss")
	);

	private static final Pattern HAS_OFFSET = Pattern.compile(".*T.*(Z|[+-]\\d{2}:?\\d{2})$");

	private NpDates(){
	}

	/**
	 * True for values that mean "no date": null, empty, the literal {@code null}, the zero dates of MySQL ({@code 0000-00-00}) and of
	 * .NET ({@code 0001-01-01}, the default of an unset DateTime, which the API sends for example in {@code DateScan}).
	 */
	public static boolean isBlankValue(String value){
		if(value == null){
			return true;
		}
		String v = value.trim();
		return v.isEmpty() || v.equalsIgnoreCase("null") || v.startsWith("0000-00-00") || v.startsWith("00.00.0000")
				|| v.startsWith("0001-01-01") || v.startsWith("01.01.0001");
	}

	/** Parses any supported date or date-time; a plain date becomes midnight. */
	public static Optional<LocalDateTime> parseDateTime(String value){
		if(isBlankValue(value)){
			return Optional.empty();
		}
		String v = value.trim();
		Optional<LocalDateTime> full = parseFullDateTime(v);
		return full.isPresent() ? full : parseDateOnly(v).map(LocalDate::atStartOfDay);
	}

	/** Parses any supported date; a date-time is truncated to its date. */
	public static Optional<LocalDate> parseDate(String value){
		if(isBlankValue(value)){
			return Optional.empty();
		}
		String v = value.trim();
		Optional<LocalDate> date = parseDateOnly(v);
		return date.isPresent() ? date : parseFullDateTime(v).map(LocalDateTime::toLocalDate);
	}

	/** Parses a time of day ({@code 8:00}, {@code 08:00}, {@code 08:00:00}); the time part of a date-time is accepted too. */
	public static Optional<LocalTime> parseTime(String value){
		if(isBlankValue(value)){
			return Optional.empty();
		}
		String v = value.trim();
		for(DateTimeFormatter f : TIME){
			try{
				return Optional.of(LocalTime.parse(v, f));
			}
			catch(RuntimeException ignored){
				// try the next format
			}
		}
		return parseFullDateTime(v).map(LocalDateTime::toLocalTime);
	}

	private static Optional<LocalDate> parseDateOnly(String v){
		for(DateTimeFormatter f : DATE){
			try{
				return Optional.of(LocalDate.parse(v, f));
			}
			catch(RuntimeException ignored){
				// try the next format
			}
		}
		return Optional.empty();
	}

	private static Optional<LocalDateTime> parseFullDateTime(String v){
		if(HAS_OFFSET.matcher(v).matches()){
			try{
				return Optional.of(OffsetDateTime.parse(v.replaceFirst("([+-]\\d{2})(\\d{2})$", "$1:$2")).toLocalDateTime());
			}
			catch(RuntimeException ignored){
				// fall through to the plain formats
			}
		}
		for(DateTimeFormatter f : DATE_TIME){
			try{
				return Optional.of(LocalDateTime.parse(v, f));
			}
			catch(RuntimeException ignored){
				// try the next format
			}
		}
		return Optional.empty();
	}
}
