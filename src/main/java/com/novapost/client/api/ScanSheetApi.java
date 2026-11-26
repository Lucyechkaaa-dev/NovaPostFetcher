package com.novapost.client.api;

import com.novapost.client.NovaPostClient;
import com.novapost.client.NpCall;
import com.novapost.client.NpModel;
import com.novapost.model.scansheet.*;

/**
 * Facade for the "ScanSheet" section of the Nova Post API documentation
 * (https://developers.novaposhta.ua/view/model/a46fc4f4-8512-11ec-8ced-005056b2dbe1).
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java.
 */
public final class ScanSheetApi{

	private final NovaPostClient client;

	public ScanSheetApi(NovaPostClient client){
		this.client = client;
	}

	/**
	 * {@code ScanSheetGeneral/insertDocuments} (docs pages a482293c).
	 */
	public NpCall<ScanSheetInsertResult> insertDocuments(ScanSheetInsertRequest request){
		return client.call(NpModel.SCAN_SHEET, "insertDocuments", request, ScanSheetInsertResult.class);
	}

	/**
	 * {@code ScanSheetGeneral/getScanSheet} (docs pages a4abdd36).
	 */
	public NpCall<ScanSheet> getScanSheet(ScanSheetRequest request){
		return client.call(NpModel.SCAN_SHEET, "getScanSheet", request, ScanSheet.class);
	}

	/**
	 * {@code ScanSheetGeneral/getScanSheetList} (docs pages a4d93a89).
	 */
	public NpCall<ScanSheetListItem> getScanSheetList(){
		return client.call(NpModel.SCAN_SHEET, "getScanSheetList", null, ScanSheetListItem.class);
	}

	/**
	 * {@code ScanSheetGeneral/deleteScanSheet} (docs pages a50e049b).
	 */
	public NpCall<ScanSheetDeleteResult> deleteScanSheet(ScanSheetDeleteRequest request){
		return client.call(NpModel.SCAN_SHEET, "deleteScanSheet", request, ScanSheetDeleteResult.class);
	}

	/**
	 * {@code ScanSheetGeneral/removeDocuments} (docs pages a53dea8a).
	 */
	public NpCall<ScanSheetRemoveDocumentsResult> removeDocuments(ScanSheetRemoveDocumentsRequest request){
		return client.call(NpModel.SCAN_SHEET, "removeDocuments", request, ScanSheetRemoveDocumentsResult.class);
	}
}
