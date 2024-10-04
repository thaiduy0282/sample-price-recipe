package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscountDetails  {

	private static final long serialVersionUID = -6821843887490141668L;

	private String adjustmentType;

	private Double adjustmentValue;

	private Double appliedOnAmount;

	private Double afterAdjustment;

	private Double netPrice;

	private long discountDate;

	private String discountSource;

	private String discountCode;

	private String productConfigurationId;

	private String lineItemId;

	private int sequence;


	private String referenceId;


	private String recipeId;

	private String appliedTo;
}
