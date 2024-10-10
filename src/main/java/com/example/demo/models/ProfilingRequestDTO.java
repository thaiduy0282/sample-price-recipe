package com.example.demo.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProfilingRequestDTO {
private List<LineItem> lineItems;
private List<DiscountDetails> discountDetails;
private String profileName;
private Quote quote;
private boolean repricing;
private String productConfigurationId;
}
