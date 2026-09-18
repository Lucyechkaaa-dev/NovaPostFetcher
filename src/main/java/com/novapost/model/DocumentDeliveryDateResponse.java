package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentDeliveryDateResponse(
		@JsonProperty("DeliveryDate") Map<String, Object> deliveryDate
){

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

	public LocalDateTime getDeliveryDateTime(){
		if(deliveryDate != null && deliveryDate.get("date") != null){
			String dateStr = String.valueOf(deliveryDate.get("date"));
			try{
				return LocalDateTime.parse(dateStr, FORMATTER);
			}
			catch(Exception e){
				// Fallback for standard ISO or variable millisecond lengths
				try{
					return LocalDateTime.parse(dateStr.replace(" ", "T"));
				}
				catch(Exception ignored){
				}
			}
		}
		return null;
	}

	public String getFormattedDate(){
		if(deliveryDate != null && deliveryDate.containsKey("date")){
			return String.valueOf(deliveryDate.get("date"));
		}
		return null;
	}
}
