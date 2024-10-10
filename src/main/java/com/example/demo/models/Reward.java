package com.example.demo.models;

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

public class Reward {

	private String rewardExpression; // e.g., "product.name=abc && product.quantity=1"
	private String rewardType;         // e.g., "percentageDiscount" or "fixedDiscount"
	private double rewardValue;        // Discount value (e.g., 100% or $20)
	private double maxDiscount;        // Maximum discount limit (optional)
}