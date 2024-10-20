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
public class Condition {
    // apply the condition like Buy >= 2 Tshirt, >= 3 Vest,...
    private String objectId;    // E.g., productId=1234abc
    private String expression;   // E.g., category.name=Tshirt && product.name=ABC
    private int value;        // The value to compare with
}

//    private String objectName;   // E.g., product, category, etc.
//    private String objectField;  // E.g., name, quantity
//    private String operator;     // E.g., ==, >=, <=