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

public class BuyConditionGroup {
    // Buy Section: Defines the conditions for triggering the adjustment
    private BuySection buySection;

    // Get Section: Defines the adjustment to be applied if the BuySection is satisfied
    private GetSection getSection;
    //endregion
}