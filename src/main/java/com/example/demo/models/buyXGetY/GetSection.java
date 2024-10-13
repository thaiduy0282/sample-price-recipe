package com.example.demo.models.buyXGetY;

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

public class GetSection {
    // TODO: For now support just one adjustment per conditions only
    // Rewards (discounts or markups) to apply if the BuySection is satisfied
    private List<Adjustment> adjustments;
}