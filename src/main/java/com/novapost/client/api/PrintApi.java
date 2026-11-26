package com.novapost.client.api;

import com.novapost.client.NovaPostClient;
import com.novapost.model.PrintFormat;
import com.novapost.model.PrintRequest;
import com.novapost.model.PrintableDocument;

import java.io.IOException;
import java.util.List;

/**
 * Facade for the "Друк" (printing) section of the documentation ({@code PrintGeneral}, see
 * https://developers.novaposhta.ua/view/model/a3fe2fdd-8512-11ec-8ced-005056b2dbe1). Printing is not a JSON call: it downloads
 * files from {@code my.novaposhta.ua} using URLs that contain the API key.
 */
public final class PrintApi{

	private final NovaPostClient client;

	public PrintApi(NovaPostClient client){
		this.client = client;
	}

	/** Downloads the file the request describes (PDF or HTML). */
	public byte[] download(PrintRequest request) throws IOException, InterruptedException{
		return client.print(request);
	}

	/** Downloads and wraps the file. */
	public PrintableDocument document(PrintRequest request) throws IOException, InterruptedException{
		return PrintableDocument.of(download(request), null, request.ids());
	}

	/** One of the ready-made layouts for one or several documents (a scan sheet takes exactly one). */
	public PrintableDocument document(PrintFormat format, List<String> ids) throws IOException, InterruptedException{
		return PrintableDocument.of(download(format.toRequest(ids)), format, ids);
	}

	/** The request for a marking on an A4 sheet with 6 labels ({@code printMarking85x85}, {@code pdf8}). */
	public static PrintRequest.Builder markingA4(List<String> ids){
		return PrintRequest.builder().doc(PrintRequest.Doc.MARKING_85X85).type(PrintRequest.Type.PDF8).ids(ids);
	}

	/** The request for a marking for a Zebra printer ({@code printMarking100x100}, {@code zebra}). */
	public static PrintRequest.Builder markingZebra(List<String> ids){
		return PrintRequest.builder().doc(PrintRequest.Doc.MARKING_100X100).type(PrintRequest.Type.PDF).zebra(true).ids(ids);
	}

	/** The request for the express waybill itself ({@code printDocument}). */
	public static PrintRequest.Builder waybill(List<String> ids){
		return PrintRequest.builder().doc(PrintRequest.Doc.DOCUMENT).type(PrintRequest.Type.PDF).ids(ids);
	}

	/** The request for a scan sheet (one Ref or number). */
	public static PrintRequest.Builder scanSheet(String refOrNumber){
		return PrintRequest.builder().doc(PrintRequest.Doc.SCAN_SHEET).type(PrintRequest.Type.PDF).id(refOrNumber);
	}
}
