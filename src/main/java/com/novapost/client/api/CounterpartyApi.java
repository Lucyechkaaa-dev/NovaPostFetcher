package com.novapost.client.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.novapost.client.NovaPostClient;
import com.novapost.client.NpCall;
import com.novapost.client.NpModel;
import com.novapost.model.counterparty.CounterpartySaveRequest;
import com.novapost.model.counterparty.Counterparty;
import com.novapost.model.counterparty.CounterpartyUpdateRequest;
import com.novapost.model.common.RefRequest;
import com.novapost.model.common.RefResult;
import com.novapost.model.counterparty.CounterpartyAddressesRequest;
import com.novapost.model.counterparty.CounterpartyAddress;
import com.novapost.model.counterparty.CounterpartyOptions;
import com.novapost.model.counterparty.CounterpartyContactPersonsRequest;
import com.novapost.model.contact.ContactPerson;
import com.novapost.model.counterparty.CounterpartyListRequest;

/**
 * Facade for the "Counterparty" section of the Nova Post API documentation
 * (https://developers.novaposhta.ua/view/model/a28f4b04-8512-11ec-8ced-005056b2dbe1).
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java.
 */
public final class CounterpartyApi{

	private final NovaPostClient client;

	public CounterpartyApi(NovaPostClient client){
		this.client = client;
	}

	/**
	 * {@code CounterpartyGeneral/save} (docs pages 0ae5dd75,b0fdf818,bc3c44c7).
	 */
	public NpCall<Counterparty> save(CounterpartySaveRequest request){
		return client.call(NpModel.COUNTERPARTY, "save", request, Counterparty.class);
	}

	/**
	 * {@code CounterpartyGeneral/update} (docs pages a2c3c708).
	 */
	public NpCall<Counterparty> update(CounterpartyUpdateRequest request){
		return client.call(NpModel.COUNTERPARTY, "update", request, Counterparty.class);
	}

	/**
	 * {@code CounterpartyGeneral/delete} (docs pages a2eb27e8).
	 * <p>Deletes a recipient counterparty only; a sender counterparty has to be deleted through support.
	 */
	public NpCall<RefResult> delete(RefRequest request){
		return client.call(NpModel.COUNTERPARTY, "delete", request, RefResult.class);
	}

	/**
	 * {@code CounterpartyGeneral/getCounterpartyAddresses} (docs pages a30dbb7c).
	 */
	public NpCall<CounterpartyAddress> getCounterpartyAddresses(CounterpartyAddressesRequest request){
		return client.call(NpModel.COUNTERPARTY, "getCounterpartyAddresses", request, CounterpartyAddress.class);
	}

	/**
	 * {@code CounterpartyGeneral/getCounterpartyOptions} (docs pages a332efbf).
	 */
	public NpCall<CounterpartyOptions> getCounterpartyOptions(RefRequest request){
		return client.call(NpModel.COUNTERPARTY, "getCounterpartyOptions", request, CounterpartyOptions.class);
	}

	/**
	 * {@code CounterpartyGeneral/getCounterpartyContactPersons} (docs pages a3575a67).
	 * <p>At most 100 records per page. The documentation recommends refreshing this directory once a day.
	 */
	public NpCall<ContactPerson> getCounterpartyContactPersons(CounterpartyContactPersonsRequest request){
		return client.call(NpModel.COUNTERPARTY, "getCounterpartyContactPersons", request, ContactPerson.class);
	}

	/**
	 * {@code CounterpartyGeneral/getCounterparties} (docs pages a37a06df).
	 * <p>At most 100 records per page. The documentation recommends refreshing this directory once a day.
	 */
	public NpCall<Counterparty> getCounterparties(CounterpartyListRequest request){
		return client.call(NpModel.COUNTERPARTY, "getCounterparties", request, Counterparty.class);
	}
}
