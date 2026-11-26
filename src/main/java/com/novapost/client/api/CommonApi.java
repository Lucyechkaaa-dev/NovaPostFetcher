package com.novapost.client.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.novapost.client.NovaPostClient;
import com.novapost.client.NpCall;
import com.novapost.client.NpModel;
import com.novapost.model.common.TimeIntervalRequest;
import com.novapost.model.common.TimeInterval;
import com.novapost.model.common.RefDescription;
import com.novapost.model.common.Pallet;
import com.novapost.model.common.PackListRequest;
import com.novapost.model.common.PackItem;
import com.novapost.model.common.TireWheel;
import com.novapost.model.common.CargoDescriptionRequest;
import com.novapost.model.common.CargoDescription;
import com.novapost.model.common.MessageCodeText;
import com.novapost.model.common.OwnershipForm;
import com.novapost.model.common.PackListSpecialRequest;
import com.novapost.model.common.PackListSpecialResult;
import com.novapost.model.common.PickupTimeIntervalRequest;
import com.novapost.model.common.PickupTimeInterval;

/**
 * Facade for the "Common" section of the Nova Post API documentation
 * (https://developers.novaposhta.ua/view/model/a55b2c64-8512-11ec-8ced-005056b2dbe1).
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java.
 */
public final class CommonApi{

	private final NovaPostClient client;

	public CommonApi(NovaPostClient client){
		this.client = client;
	}

	/**
	 * {@code CommonGeneral/getTimeIntervals} (docs pages a56d5c1c).
	 */
	public NpCall<TimeInterval> getTimeIntervals(TimeIntervalRequest request){
		return client.call(NpModel.COMMON, "getTimeIntervals", request, TimeInterval.class);
	}

	/**
	 * {@code CommonGeneral/getCargoTypes} (docs pages a5912a1e).
	 */
	public NpCall<RefDescription> getCargoTypes(){
		return client.call(NpModel.COMMON, "getCargoTypes", null, RefDescription.class);
	}

	/**
	 * {@code CommonGeneral/getBackwardDeliveryCargoTypes} (docs pages a5b46873).
	 */
	public NpCall<RefDescription> getBackwardDeliveryCargoTypes(){
		return client.call(NpModel.COMMON, "getBackwardDeliveryCargoTypes", null, RefDescription.class);
	}

	/**
	 * {@code CommonGeneral/getPalletsList} (docs pages a5dd575e).
	 */
	public NpCall<Pallet> getPalletsList(){
		return client.call(NpModel.COMMON, "getPalletsList", null, Pallet.class);
	}

	/**
	 * {@code CommonGeneral/getTypesOfPayersForRedelivery} (docs pages a6247f2f).
	 */
	public NpCall<RefDescription> getTypesOfPayersForRedelivery(){
		return client.call(NpModel.COMMON, "getTypesOfPayersForRedelivery", null, RefDescription.class);
	}

	/**
	 * {@code CommonGeneral/getPackList} (docs pages a6492db4).
	 * <p>The portal prints the parameter names glued to their type (Lengthstring, Widthstring, ...); the names used here are the evident intended ones. Refresh this directory monthly.
	 */
	public NpCall<PackItem> getPackList(PackListRequest request){
		return client.call(NpModel.COMMON, "getPackList", request, PackItem.class);
	}

	/**
	 * {@code CommonGeneral/getTiresWheelsList} (docs pages a66fada0).
	 */
	public NpCall<TireWheel> getTiresWheelsList(){
		return client.call(NpModel.COMMON, "getTiresWheelsList", null, TireWheel.class);
	}

	/**
	 * {@code CommonGeneral/getCargoDescriptionList} (docs pages a697db47).
	 * <p>At most 500 records per page (Pager.CARGO_DESCRIPTIONS).
	 */
	public NpCall<CargoDescription> getCargoDescriptionList(CargoDescriptionRequest request){
		return client.call(NpModel.COMMON, "getCargoDescriptionList", request, CargoDescription.class);
	}

	/**
	 * {@code CommonGeneral/getMessageCodeText} (docs pages a6bce5a1).
	 */
	public NpCall<MessageCodeText> getMessageCodeText(){
		return client.call(NpModel.COMMON, "getMessageCodeText", null, MessageCodeText.class);
	}

	/**
	 * {@code CommonGeneral/getServiceTypes} (docs pages a6e189f7).
	 */
	public NpCall<RefDescription> getServiceTypes(){
		return client.call(NpModel.COMMON, "getServiceTypes", null, RefDescription.class);
	}

	/**
	 * {@code CommonGeneral/getOwnershipFormsList} (docs pages a754ff0d).
	 */
	public NpCall<OwnershipForm> getOwnershipFormsList(){
		return client.call(NpModel.COMMON, "getOwnershipFormsList", null, OwnershipForm.class);
	}

	/**
	 * {@code CommonGeneral/getPackListSpecial} (docs pages affdad3e).
	 */
	public NpCall<PackListSpecialResult> getPackListSpecial(PackListSpecialRequest request){
		return client.call(NpModel.COMMON_GENERAL, "getPackListSpecial", request, PackListSpecialResult.class);
	}

	/**
	 * {@code CommonGeneral/getPickupTimeIntervals} (docs pages c4389a2c).
	 */
	public NpCall<PickupTimeInterval> getPickupTimeIntervals(PickupTimeIntervalRequest request){
		return client.call(NpModel.COMMON_GENERAL, "getPickupTimeIntervals", request, PickupTimeInterval.class);
	}
}
