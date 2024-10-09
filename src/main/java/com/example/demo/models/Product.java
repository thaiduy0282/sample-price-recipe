package com.example.demo.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
	private String id;
	private static final long serialVersionUID = 6529249104361519153L;
	private String productFamilyId;
	private String productFamily;
	private String productGroupId;

	private String type;

	private String productCode;
	private String minQuantity;
	private String maxQuantity;
	private String defaultQuantity;
	private String autoUpdateQuantity;
	private String inclusionCriteria;
	private String productGroupName;
	private String image;
	public boolean isSearchable;
	@JsonProperty("isDefaultOption")
	private boolean isDefaultOption;
	public boolean isRequired;
	public boolean isHasAttributes;
	public long attributeGroupCount;

	private String model;
	public boolean isHasFeatureAttributeGroup;
	public boolean addOn;
	private String productTypeId;
	private String relatedPriceListId;
	private String productUoM;
	private Tag tag;
}