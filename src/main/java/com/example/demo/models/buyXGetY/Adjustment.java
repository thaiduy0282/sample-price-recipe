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
    private double value;                 // Percentage or Fixed Amount
    private double maxAdjustmentAmount;   // Limit on total adjustment amount per line item (optional)

}