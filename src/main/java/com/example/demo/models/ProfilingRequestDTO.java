package com.example.demo.models;


import lombok.Data;

import java.util.List;

@Data
public class ProfilingRequestDTO {
private List<LineItem> lineItems;
private List<DiscountDetails> discountDetails;
private String profileName;
private Quote quote;
private boolean repricing;
private String productConfigurationId;
}
