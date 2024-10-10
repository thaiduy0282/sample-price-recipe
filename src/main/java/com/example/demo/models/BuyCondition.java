package com.example.demo.models;

import com.example.demo.common.LogicalOperator;
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
public class BuyCondition {
	private String expression; // e.g., "product.name=abc && product.quantity>3"
	private LogicalOperator logicalOperator; // AND or OR for combining conditions
}