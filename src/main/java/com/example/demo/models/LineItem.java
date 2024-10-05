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
	private String model;
	private String sellingFrequency;
	private String tags;
	private String byCategory;
	private String productFamily;
	private Double netPrice;
	private String configurationName;
	private String locationName;
	private int quantity;
}
