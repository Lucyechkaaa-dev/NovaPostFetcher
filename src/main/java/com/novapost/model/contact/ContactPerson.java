package com.novapost.model.contact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response model for {@code CounterpartyGeneral/getCounterpartyContactPersons}, {@code ContactPersonGeneral/save}, {@code ContactPersonGeneral/update}.
 * Docs: https://developers.novaposhta.ua/view/model/a39040c4-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ContactPerson(
		@JsonProperty("Description") String description,
		@JsonProperty("Ref") String ref,
		@JsonProperty("Phones") String phones,
		@JsonProperty("Email") String email,
		@JsonProperty("LastName") String lastName,
		@JsonProperty("FirstName") String firstName,
		@JsonProperty("MiddleName") String middleName
){
}
