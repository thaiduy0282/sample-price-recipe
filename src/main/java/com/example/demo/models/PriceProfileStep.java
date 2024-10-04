package com.example.demo.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PriceProfileStep {
	
	private String pricePoint;
	private int sequence;
	private String pricingMethod;
	private String scope;
	private String scopeValue;
	private String priceSetting;

}
