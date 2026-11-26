package com.novapost.model.contact;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for {@code ContactPersonGeneral/save}.
 * Docs: https://developers.novaposhta.ua/view/model/a39040c4-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ContactPersonSaveRequest(
		@JsonProperty("CounterpartyRef") String counterpartyRef,
		@JsonProperty("FirstName") String firstName,
		@JsonProperty("LastName") String lastName,
		@JsonProperty("MiddleName") String middleName,
		@JsonProperty("Phone") String phone
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String counterpartyRef;
		private String firstName;
		private String lastName;
		private String middleName;
		private String phone;

		public Builder counterpartyRef(String counterpartyRef){ this.counterpartyRef = counterpartyRef; return this; }
		public Builder firstName(String firstName){ this.firstName = firstName; return this; }
		public Builder lastName(String lastName){ this.lastName = lastName; return this; }
		public Builder middleName(String middleName){ this.middleName = middleName; return this; }
		public Builder phone(String phone){ this.phone = phone; return this; }

		public ContactPersonSaveRequest build(){
			return new ContactPersonSaveRequest(counterpartyRef, firstName, lastName, middleName, phone);
		}
	}
}
