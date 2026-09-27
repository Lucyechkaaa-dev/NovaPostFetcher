package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InternetDocumentListFilter(
		@JsonProperty("DateTime") String dateTime,
		@JsonProperty("DateTimeFrom") String dateTimeFrom,
		@JsonProperty("DateTimeTo") String dateTimeTo,
		@JsonProperty("Page") Integer page,
		@JsonProperty("Limit") Integer limit,
		@JsonProperty("GetFullList") Integer getFullList
){

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	public static InternetDocumentListFilter byDateRange(LocalDate from, LocalDate to, int page, int limit){
		String fromStr = from != null ? from.format(DATE_FORMATTER) : null;
		String toStr = to != null ? to.format(DATE_FORMATTER) : null;
		return new InternetDocumentListFilter(null, fromStr, toStr, page, limit, 1);
	}

	public static InternetDocumentListFilter byDay(LocalDate day, int page, int limit){
		if(day == null){
			return byDateRange(null, null, page, limit);
		}
		String formatted = day.format(DATE_FORMATTER);
		return new InternetDocumentListFilter(null, formatted + " 00:00:00", formatted + " 23:59:59", page, limit, 1);
	}

	public static InternetDocumentListFilter of(String dateTimeFrom, String dateTimeTo, int page, int limit){
		return new InternetDocumentListFilter(null, dateTimeFrom, dateTimeTo, page, limit, 1);
	}
}
