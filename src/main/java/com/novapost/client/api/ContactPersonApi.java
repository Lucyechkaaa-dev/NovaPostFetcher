package com.novapost.client.api;

import com.novapost.client.NovaPostClient;
import com.novapost.client.NpCall;
import com.novapost.client.NpModel;
import com.novapost.model.common.RefRequest;
import com.novapost.model.common.RefResult;
import com.novapost.model.contact.ContactPerson;
import com.novapost.model.contact.ContactPersonSaveRequest;
import com.novapost.model.contact.ContactPersonUpdateRequest;

/**
 * Facade for the "ContactPerson" section of the Nova Post API documentation
 * (https://developers.novaposhta.ua/view/model/a39040c4-8512-11ec-8ced-005056b2dbe1).
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java.
 */
public final class ContactPersonApi{

	private final NovaPostClient client;

	public ContactPersonApi(NovaPostClient client){
		this.client = client;
	}

	/**
	 * {@code ContactPersonGeneral/save} (docs pages a3a25bda).
	 */
	public NpCall<ContactPerson> save(ContactPersonSaveRequest request){
		return client.call(NpModel.CONTACT_PERSON, "save", request, ContactPerson.class);
	}

	/**
	 * {@code ContactPersonGeneral/update} (docs pages a3c5a577).
	 * <p>A contact person can be edited only until a waybill has been created with this contact person.
	 */
	public NpCall<ContactPerson> update(ContactPersonUpdateRequest request){
		return client.call(NpModel.CONTACT_PERSON, "update", request, ContactPerson.class);
	}

	/**
	 * {@code ContactPersonGeneral/delete} (docs pages a3ea91c8).
	 */
	public NpCall<RefResult> delete(RefRequest request){
		return client.call(NpModel.CONTACT_PERSON, "delete", request, RefResult.class);
	}
}
