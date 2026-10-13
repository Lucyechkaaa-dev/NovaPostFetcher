package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Ergonomic descriptor for package and cargo properties.
 * Designed to be provided independently by coworkers managing physical warehouse parcels.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CargoDetails(
		String cargoType,
		String description,
		String weight,
		String cost,
		String seatsAmount,
		String volumeGeneral,
		List<InternetDocumentSaveRequest.OptionSeat> optionsSeat
){

	public static final String DEFAULT_CARGO_TYPE = "Parcel";
	public static final String DEFAULT_DESCRIPTION = "Goods";
	public static final String DEFAULT_WEIGHT = "1.0";
	public static final String DEFAULT_COST = "300";
	public static final String DEFAULT_SEATS_AMOUNT = "1";

	public static CargoDetails ofPlaceholder(){
		return new CargoDetails(
				DEFAULT_CARGO_TYPE,
				DEFAULT_DESCRIPTION,
				DEFAULT_WEIGHT,
				DEFAULT_COST,
				DEFAULT_SEATS_AMOUNT,
				null,
				null
		);
	}

	public static CargoDetails ofSimple(String description, double weightKg, double declaredCostUah){
		return new CargoDetails(
				DEFAULT_CARGO_TYPE,
				description,
				String.valueOf(weightKg),
				String.valueOf((int) Math.round(declaredCostUah)),
				"1",
				null,
				null
		);
	}

	public static CargoDetails ofBox(String description, double weightKg, double declaredCostUah,
	                                 int widthCm, int lengthCm, int heightCm){
		InternetDocumentSaveRequest.OptionSeat seat = InternetDocumentSaveRequest.OptionSeat.ofDimensions(
				String.valueOf(widthCm),
				String.valueOf(lengthCm),
				String.valueOf(heightCm),
				String.valueOf(weightKg)
		);
		return new CargoDetails(
				DEFAULT_CARGO_TYPE,
				description,
				String.valueOf(weightKg),
				String.valueOf((int) Math.round(declaredCostUah)),
				"1",
				null,
				List.of(seat)
		);
	}

	public static Builder builder(){
		return new Builder();
	}

	public static class Builder{
		private String cargoType = DEFAULT_CARGO_TYPE;
		private String description = DEFAULT_DESCRIPTION;
		private String weight = DEFAULT_WEIGHT;
		private String cost = DEFAULT_COST;
		private String seatsAmount = DEFAULT_SEATS_AMOUNT;
		private String volumeGeneral;
		private List<InternetDocumentSaveRequest.OptionSeat> optionsSeat;

		public Builder cargoType(String cargoType){ this.cargoType = cargoType; return this; }
		public Builder description(String description){ this.description = description; return this; }
		public Builder weight(String weight){ this.weight = weight; return this; }
		public Builder weight(double weightKg){ this.weight = String.valueOf(weightKg); return this; }
		public Builder cost(String cost){ this.cost = cost; return this; }
		public Builder cost(double declaredCostUah){ this.cost = String.valueOf((int) Math.round(declaredCostUah)); return this; }
		public Builder seatsAmount(String seatsAmount){ this.seatsAmount = seatsAmount; return this; }
		public Builder seatsAmount(int seatsAmount){ this.seatsAmount = String.valueOf(seatsAmount); return this; }
		public Builder volumeGeneral(String volumeGeneral){ this.volumeGeneral = volumeGeneral; return this; }
		public Builder optionsSeat(List<InternetDocumentSaveRequest.OptionSeat> optionsSeat){ this.optionsSeat = optionsSeat; return this; }

		public Builder addSeat(int widthCm, int lengthCm, int heightCm, double weightKg){
			if(this.optionsSeat == null){
				this.optionsSeat = new java.util.ArrayList<>();
			}
			this.optionsSeat.add(InternetDocumentSaveRequest.OptionSeat.ofDimensions(
					String.valueOf(widthCm), String.valueOf(lengthCm), String.valueOf(heightCm), String.valueOf(weightKg)
			));
			return this;
		}

		public CargoDetails build(){
			return new CargoDetails(cargoType, description, weight, cost, seatsAmount, volumeGeneral, optionsSeat);
		}
	}
}
