package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = false)

public class PriceRecipeRange {
	private double startTier;
	private double endTier;
	private String dealStrategy;
	private String applicationType;
	private String applicationValue;
	List<String > sourceDimensionName;
	List<String > targetDimensionName;
}
