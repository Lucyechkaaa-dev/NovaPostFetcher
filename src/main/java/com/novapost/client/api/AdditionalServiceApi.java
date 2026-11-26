package com.novapost.client.api;

import com.novapost.client.NovaPostClient;
import com.novapost.client.NpCall;
import com.novapost.client.NpModel;
import com.novapost.model.common.RefRequest;
import com.novapost.model.service.*;

/**
 * Facade for the "AdditionalService" section of the Nova Post API documentation
 * (https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1).
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java.
 */
public final class AdditionalServiceApi{

	private final NovaPostClient client;

	public AdditionalServiceApi(NovaPostClient client){
		this.client = client;
	}

	/**
	 * {@code AdditionalServiceGeneral/checkPossibilityForRedirecting} (docs pages a8d29fc2).
	 */
	public NpCall<RedirectingPossibility> checkRedirectingPossibility(RedirectingPossibilityRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "checkPossibilityForRedirecting", request, RedirectingPossibility.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/save} (docs pages 98acb0f6).
	 */
	public NpCall<ServiceOrderResult> createRedirecting(RedirectingOrderRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "save", request, ServiceOrderResult.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/checkPossibilityForRedirecting} (docs pages 1934683d).
	 */
	public NpCall<RedirectingEditCheck> checkRedirectingEditPossibility(RedirectingEditCheckRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "checkPossibilityForRedirecting", request, RedirectingEditCheck.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/update} (docs pages c61d3597).
	 */
	public NpCall<RedirectingUpdateResult> updateRedirecting(RedirectingUpdateRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "update", request, RedirectingUpdateResult.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/getRedirectionOrdersList} (docs pages a8faa2e6).
	 */
	public NpCall<RedirectingOrder> getRedirectingOrders(ServiceOrderListRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "getRedirectionOrdersList", request, RedirectingOrder.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/save} (docs pages db0e1b90).
	 */
	public NpCall<ServicePricing> calculateRedirecting(RedirectingCalculateRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "save", request, ServicePricing.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/delete} (docs pages a85bb34b).
	 */
	public NpCall<ServiceOrderDeleteResult> deleteOrder(RefRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "delete", request, ServiceOrderDeleteResult.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/CheckPossibilityCreateReturn} (docs pages a778f519).
	 */
	public NpCall<ReturnPossibility> checkReturnPossibility(ReturnPossibilityRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "CheckPossibilityCreateReturn", request, ReturnPossibility.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/save} (docs pages a7fb4a3a).
	 */
	public NpCall<ServiceOrderResult> createReturn(ReturnOrderRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "save", request, ServiceOrderResult.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/CheckPossibilityCreateReturn} (docs pages 06b78d02).
	 * <p>The parameter table of the response is a placeholder in the documentation ("NoName"); the fields below are those of the documented example.
	 */
	public NpCall<ReturnEditCheck> checkReturnEditPossibility(ReturnEditCheckRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "CheckPossibilityCreateReturn", request, ReturnEditCheck.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/update} (docs pages 2c81f3bc).
	 */
	public NpCall<ServicePricing> updateReturn(ReturnOrderUpdateRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "update", request, ServicePricing.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/getReturnOrdersList} (docs pages a82d087c).
	 */
	public NpCall<ReturnOrder> getReturnOrders(ServiceOrderListRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "getReturnOrdersList", request, ReturnOrder.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/save} (docs pages 4ee79a8d).
	 */
	public NpCall<ServicePricing> calculateReturn(ReturnCalculateRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "save", request, ServicePricing.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/getReturnReasons} (docs pages a7a6bacb).
	 */
	public NpCall<ReturnReason> getReturnReasons(){
		return client.call(NpModel.ADDITIONAL_SERVICE, "getReturnReasons", null, ReturnReason.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/getReturnReasonsSubtypes} (docs pages a7cb69ee).
	 */
	public NpCall<ReturnReasonSubtype> getReturnReasonSubtypes(ReturnReasonSubtypesRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "getReturnReasonsSubtypes", request, ReturnReasonSubtype.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/getChangeEWOrdersList} (docs pages a8ad1dc7).
	 */
	public NpCall<ChangeEwOrder> getChangeEwOrders(ChangeEwOrderListRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "getChangeEWOrdersList", request, ChangeEwOrder.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/CheckPossibilityChangeEW} (docs pages a886b776).
	 */
	public NpCall<ChangeEwPossibility> checkChangeEwPossibility(ChangeEwPossibilityRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "CheckPossibilityChangeEW", request, ChangeEwPossibility.class);
	}

	/**
	 * {@code AdditionalServiceGeneral/save} (docs pages c09f1b02).
	 */
	public NpCall<ServiceOrderResult> changeEw(ChangeEwRequest request){
		return client.call(NpModel.ADDITIONAL_SERVICE, "save", request, ServiceOrderResult.class);
	}
}
