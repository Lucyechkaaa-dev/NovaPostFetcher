package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InternetDocumentListFilter(
		@JsonProperty("DateTime") @JsonFormat(pattern = "dd.MM.yyyy") LocalDate dateTime,
		@JsonProperty("DateTimeFrom") @JsonFormat(pattern = "dd.MM.yyyy") LocalDate dateTimeFrom,
		@JsonProperty("DateTimeTo") @JsonFormat(pattern = "dd.MM.yyyy") LocalDate dateTimeTo,
		@JsonProperty("Page") Integer page,
		@JsonProperty("Limit") Integer limit,
		@JsonProperty("GetFullList") Integer getFullList
){

	public static InternetDocumentListFilter byDateRange(LocalDate from, LocalDate to, int page, int limit){
		return new InternetDocumentListFilter(null, from, to, page, limit, 1);
	}
}
