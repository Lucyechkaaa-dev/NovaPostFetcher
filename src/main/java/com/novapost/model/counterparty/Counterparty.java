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
 * Response model for {@code CounterpartyGeneral/save}, {@code CounterpartyGeneral/update}, {@code CounterpartyGeneral/getCounterparties}.
 * Docs: https://developers.novaposhta.ua/view/model/a28f4b04-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Counterparty(
		@JsonProperty("Ref") String ref,
		@JsonProperty("Description") String description,
		@JsonProperty("FirstName") String firstName,
		@JsonProperty("MiddleName") String middleName,
		@JsonProperty("LastName") String lastName,
		@JsonProperty("Counterparty") String counterparty,
		@JsonProperty("OwnershipForm") String ownershipForm,
		@JsonProperty("OwnershipFormDescription") String ownershipFormDescription,
		@JsonProperty("EDRPOU") String edrpou,
		@JsonProperty("CounterpartyType") String counterpartyType,
		@JsonProperty("ContactPerson") JsonNode contactPerson,
		@JsonProperty("City") String city,
		@JsonProperty("OwnershipFormRef") String ownershipFormRef
){
}
