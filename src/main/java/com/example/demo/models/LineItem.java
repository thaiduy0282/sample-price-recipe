package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class LineItem  {
	private static final long serialVersionUID = 8204041143290681328L;
	private String Id;
	private String model;
	private String sellingFrequency;
	private String tags;
	private String byCategory;
	private String productFamily;
	private Double netPrice;
	private String configurationName;
	private String timeDimensionName;
	private Double quantity;
	private String productId;
}
