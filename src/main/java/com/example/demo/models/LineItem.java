package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class LineItem  {
	private static final long serialVersionUID = 8204041143290681328L;
	private String id;
	private String model;
	private String sellingFrequency;
	private String tags;
	private String byCategory;
	private String productFamily;
	private Double listPrice;
	private Double referencePrice;
	private Double contractPrice;
	private Double netPrice;
	private Double unitPrice;
	private String configurationName;
	private String timeDimensionName;
	private Double quantity;
	private String productId;
	private String locationName;
}
