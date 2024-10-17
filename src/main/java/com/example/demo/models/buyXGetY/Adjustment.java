package com.example.demo.models.buyXGetY;

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

public class Adjustment {

    private String productId;            // Apply adjustment to a specific product line item
    private String dealStrategy;        // Discount or Markup
    private String applicationType;     // Percentage or Fixed Amount
    private int requiredQuantity;    // Max item apply adjustment once
    private double applicationValue;

}