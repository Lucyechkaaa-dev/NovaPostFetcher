package com.novapost.client.api;

import com.novapost.client.NovaPostClient;
import com.novapost.client.NpCall;
import com.novapost.client.NpModel;
import com.novapost.model.*;
import com.novapost.model.document.EwTemplateListRequest;
import com.novapost.model.document.GenerateReportItem;
import com.novapost.model.document.GenerateReportRequest;
import com.novapost.model.document.InternetDocument;

/**
 * Facade for the "InternetDocument" section of the Nova Post API documentation
 * (https://developers.novaposhta.ua/view/model/a90d323c-8512-11ec-8ced-005056b2dbe1).
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java.
 */
public final class InternetDocumentApi{

	private final NovaPostClient client;

	public InternetDocumentApi(NovaPostClient client){
		this.client = client;
	}

	/**
	 * {@code InternetDocumentGeneral/getDocumentPrice} (docs pages a91f115b).
	 */
	public NpCall<DocumentPriceResponse> getDocumentPrice(DocumentPriceRequest request){
		return client.call(NpModel.INTERNET_DOCUMENT, "getDocumentPrice", request, DocumentPriceResponse.class);
	}

	/**
	 * {@code InternetDocumentGeneral/getDocumentDeliveryDate} (docs pages a941c714).
	 * <p>The documentation says the waybill's services (backward delivery, daily delivery and others) must be given in the request as mandatory elements of the estimate, but it names no parameter for them: only DateTime, ServiceType, CitySender and CityRecipient are documented, and the go-novapost SDK declares the same four.
	 * <p>If a service parameter turns out to be needed, send it through NovaPostClient.call(NpModel.INTERNET_DOCUMENT, "getDocumentDeliveryDate", aMap, JsonNode.class).
	 * <p>DateTime is a plain date (dd.MM.yyyy) by default; the documentation's example sends date and time (yyyy-MM-dd HH:mm:ss), see DocumentDeliveryDateRequest.ofDateTime.
	 */
	public NpCall<DocumentDeliveryDateResponse> getDocumentDeliveryDate(DocumentDeliveryDateRequest request){
		return client.call(NpModel.INTERNET_DOCUMENT, "getDocumentDeliveryDate", request, DocumentDeliveryDateResponse.class);
	}

	/**
	 * {@code InternetDocumentGeneral/save} (docs pages a965630e,0227072e,3fdc36f1,751067b8,a19ced79,c7c8aaf5,df942798,f74a0918).
	 * <p>DateTime (the send date, dd.MM.yyyy) may not exceed today by more than 3 months; a larger value is cut back to the maximum, the request still succeeds and the answer carries a warning.
	 * <p>A waybill created through the API appears in the waybill list of the personal cabinet.
	 */
	public NpCall<InternetDocumentResponse> save(InternetDocumentSaveRequest request){
		return client.call(NpModel.INTERNET_DOCUMENT, "save", request, InternetDocumentResponse.class);
	}

	/**
	 * {@code InternetDocumentGeneral/update} (docs pages a98a4354).
	 * <p>Fill in all parameters; after the update only the ones that were modified are replaced.
	 */
	public NpCall<InternetDocumentResponse> update(InternetDocumentSaveRequest request){
		return client.call(NpModel.INTERNET_DOCUMENT, "update", request, InternetDocumentResponse.class);
	}

	/**
	 * {@code InternetDocumentGeneral/getDocumentList} (docs pages a9d22b34).
	 * <p>GetFullList=1 returns the list for the period but at most 100 records (a system limit) and needs no Page; GetFullList=0 loads page by page without a limit on the total and requires Page.
	 * <p>Without a date or a number the API returns the waybills of the current day. RedeliveryMoney=1 also returns the waybills with a backward delivery ("Грошовий переказ" / "Каса форпост").
	 */
	public NpCall<InternetDocumentListItem> getDocumentList(InternetDocumentListFilter request){
		return client.call(NpModel.INTERNET_DOCUMENT, "getDocumentList", request, InternetDocumentListItem.class);
	}

	/**
	 * {@code InternetDocumentGeneral/delete} (docs pages a9f43ff1).
	 */
	public NpCall<InternetDocumentDeleteResponse> delete(InternetDocumentDeleteRequest request){
		return client.call(NpModel.INTERNET_DOCUMENT, "delete", request, InternetDocumentDeleteResponse.class);
	}

	/**
	 * {@code InternetDocumentGeneral/generateReport} (docs pages aa1b448f).
	 */
	public NpCall<GenerateReportItem> generateReport(GenerateReportRequest request){
		return client.call(NpModel.INTERNET_DOCUMENT, "generateReport", request, GenerateReportItem.class);
	}

	/**
	 * {@code InternetDocumentGeneral/getEWTemplateList} (docs pages dfc923e9).
	 * <p>The answer is the full view of a waybill (124 fields, see InternetDocument); the documentation labels this method "documents in a courier-call request".
	 */
	public NpCall<InternetDocument> getEwTemplateList(EwTemplateListRequest request){
		return client.call(NpModel.INTERNET_DOCUMENT, "getEWTemplateList", request, InternetDocument.class);
	}
}
