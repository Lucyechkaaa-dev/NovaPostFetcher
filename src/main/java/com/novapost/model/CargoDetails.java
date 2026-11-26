package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ergonomic descriptor for package and cargo properties.
 * Designed to be provided independently by coworkers managing physical warehouse parcels.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CargoDetails(
		String cargoType,
		String description,
		BigDecimal weight,
		BigDecimal cost,
		Integer seatsAmount,
		BigDecimal volumeGeneral,
		List<InternetDocumentSaveRequest.OptionSeat> optionsSeat
){

	public static final String DEFAULT_CARGO_TYPE = "Parcel";
	public static final String DEFAULT_DESCRIPTION = "Goods";
	public static final BigDecimal DEFAULT_WEIGHT = new BigDecimal("1.0");
	public static final BigDecimal DEFAULT_COST = new BigDecimal("300");
	public static final Integer DEFAULT_SEATS_AMOUNT = 1;

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
				BigDecimal.valueOf(weightKg),
				BigDecimal.valueOf(Math.round(declaredCostUah)),
				1,
				null,
				null
		);
	}

	public static CargoDetails ofBox(String description, double weightKg, double declaredCostUah,
	                                 int widthCm, int lengthCm, int heightCm){
		InternetDocumentSaveRequest.OptionSeat seat = InternetDocumentSaveRequest.OptionSeat.ofDimensions(
				BigDecimal.valueOf(widthCm),
				BigDecimal.valueOf(lengthCm),
				BigDecimal.valueOf(heightCm),
				BigDecimal.valueOf(weightKg)
		);
		return new CargoDetails(
				DEFAULT_CARGO_TYPE,
				description,
				BigDecimal.valueOf(weightKg),
				BigDecimal.valueOf(Math.round(declaredCostUah)),
				1,
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
		private BigDecimal weight = DEFAULT_WEIGHT;
		private BigDecimal cost = DEFAULT_COST;
		private Integer seatsAmount = DEFAULT_SEATS_AMOUNT;
		private BigDecimal volumeGeneral;
		private List<InternetDocumentSaveRequest.OptionSeat> optionsSeat;

		public Builder cargoType(String cargoType){ this.cargoType = cargoType; return this; }
		public Builder description(String description){ this.description = description; return this; }
		public Builder weight(BigDecimal weight){ this.weight = weight; return this; }
		public Builder weight(double weightKg){ this.weight = BigDecimal.valueOf(weightKg); return this; }
		public Builder cost(BigDecimal cost){ this.cost = cost; return this; }
		public Builder cost(double declaredCostUah){ this.cost = BigDecimal.valueOf(Math.round(declaredCostUah)); return this; }
		public Builder seatsAmount(Integer seatsAmount){ this.seatsAmount = seatsAmount; return this; }
		public Builder volumeGeneral(BigDecimal volumeGeneral){ this.volumeGeneral = volumeGeneral; return this; }
		public Builder volumeGeneral(double volumeM3){ this.volumeGeneral = BigDecimal.valueOf(volumeM3); return this; }
		public Builder optionsSeat(List<InternetDocumentSaveRequest.OptionSeat> optionsSeat){ this.optionsSeat = optionsSeat; return this; }

		public Builder addSeat(int widthCm, int lengthCm, int heightCm, double weightKg){
			if(this.optionsSeat == null){
				this.optionsSeat = new java.util.ArrayList<>();
			}
			this.optionsSeat.add(InternetDocumentSaveRequest.OptionSeat.ofDimensions(
					BigDecimal.valueOf(widthCm), BigDecimal.valueOf(lengthCm), BigDecimal.valueOf(heightCm), BigDecimal.valueOf(weightKg)
			));
			return this;
		}

		public CargoDetails build(){
			return new CargoDetails(cargoType, description, weight, cost, seatsAmount, volumeGeneral, optionsSeat);
		}
	}
}
