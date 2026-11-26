package com.novapost.client;

import java.util.List;

/**
 * The data of a successful call together with what the API said besides it. A request can succeed and still carry
 * warnings, for example when {@code DateTime} of a waybill was cut back to "today + 3 months".
 */
public record NpResult<T>(List<T> data, List<String> warnings, List<String> warningCodes, List<String> infoCodes){

	public NpResult{
		data = data != null ? data : List.of();
		warnings = warnings != null ? warnings : List.of();
		warningCodes = warningCodes != null ? warningCodes : List.of();
		infoCodes = infoCodes != null ? infoCodes : List.of();
	}

	public boolean hasWarnings(){
		return !warnings.isEmpty();
	}
}
