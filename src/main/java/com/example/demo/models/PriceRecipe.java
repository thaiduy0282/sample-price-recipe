package com.example.demo.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = false)

public class PriceRecipe {

	private String id;
	private String scope;
	private String scopeValue;
	private String priceSetting;
	private String type;
	private String dealStrategy;
	private String priceApplicationON;
	private String executionSequence;
	private String pricingCondition;
	private String applicationType;
	private double applicationValue;
	private String appliedOn;
	private String priceAppliedTo;
	private String dimension;
	private List<PriceRecipeRange> ranges;
	private String voucherCode;
	private Long voucherStartDate;
	private Long voucherEndDate;

}