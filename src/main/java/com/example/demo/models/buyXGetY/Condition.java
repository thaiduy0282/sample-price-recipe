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
    // TODO: Currently, just apply the sample condition for quantity attribute like Buy >= 2 itemA, >= 3 itemB,...
    private String objectId;    // E.g., productId
//    private String objectName;   // E.g., product, category, etc.
//    private String objectField;  // E.g., name, quantity
//    private String operator;     // E.g., ==, >=, <=
    private int value;        // The value to compare with
}