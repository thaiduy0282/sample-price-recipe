package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceProfileStep {
	
	private String pricePoint;
	private int sequence;
	private String pricingMethod;
	private String scope;
	private String scopeValue;
	private String priceSetting;

}
