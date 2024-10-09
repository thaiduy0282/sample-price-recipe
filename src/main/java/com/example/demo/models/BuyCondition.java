package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class BuyCondition {
	private String conditionType; // e.g., "buyItem", "buyMoreThanPrice", "buyMoreThanQuantity", "buyDifferentItems"
	private String itemId; // For "buyItem" or "buyMoreThanQuantity"
	private double thresholdAmount; // For "buyMoreThanPrice"
	private int quantity; // For "buyMoreThanQuantity"
	private List<String> differentItemIds; // For "buyDifferentItems"
}