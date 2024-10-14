package com.example.demo.models;

import java.util.List;

import com.example.demo.models.buyXGetY.BuyConditionGroup;
import com.example.demo.models.buyXGetY.BuySection;
import com.example.demo.models.buyXGetY.GetSection;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
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
	private Voucher voucher;

	//region: Setup BuyXGetY
    private List<BuyConditionGroup> conditionGroups;

}