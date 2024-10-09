package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = false)

public class GetReward {
	private String rewardType; // e.g., "discountPercentage", "discountAmount", "freeItem", "freeShipping"
	private double discountPercentage; // For percentage discounts
	private double discountAmount; // For fixed discount amounts
	private double maxDiscount; // Max discount for percentage-based discounts
	private String rewardItemId; // The product ID for the discounted or free item
	private int freeItemQuantity; // Number of free items
	private boolean applyToTotal; // Whether the reward applies to the total order
}