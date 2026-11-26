package com.novapost.model.counterparty;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code CounterpartyGeneral/save}.
 * Docs: https://developers.novaposhta.ua/view/model/a28f4b04-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CounterpartySaveRequest(
		@JsonProperty("FirstName") String firstName,
		@JsonProperty("MiddleName") String middleName,
		@JsonProperty("LastName") String lastName,
		@JsonProperty("Phone") String phone,
		@JsonProperty("Email") String email,
		@JsonProperty("CounterpartyType") String counterpartyType,
		@JsonProperty("CounterpartyProperty") String counterpartyProperty,
		@JsonProperty("EDRPOU") String edrpou,
		@JsonProperty("CityRef") String cityRef
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String firstName;
		private String middleName;
		private String lastName;
		private String phone;
		private String email;
		private String counterpartyType;
		private String counterpartyProperty;
		private String edrpou;
		private String cityRef;

		public Builder firstName(String firstName){ this.firstName = firstName; return this; }
		public Builder middleName(String middleName){ this.middleName = middleName; return this; }
		public Builder lastName(String lastName){ this.lastName = lastName; return this; }
		public Builder phone(String phone){ this.phone = phone; return this; }
		public Builder email(String email){ this.email = email; return this; }
		public Builder counterpartyType(String counterpartyType){ this.counterpartyType = counterpartyType; return this; }
		public Builder counterpartyProperty(String counterpartyProperty){ this.counterpartyProperty = counterpartyProperty; return this; }
		public Builder edrpou(String edrpou){ this.edrpou = edrpou; return this; }
		public Builder cityRef(String cityRef){ this.cityRef = cityRef; return this; }

		public CounterpartySaveRequest build(){
			return new CounterpartySaveRequest(firstName, middleName, lastName, phone, email, counterpartyType, counterpartyProperty, edrpou, cityRef);
		}
	}
}
