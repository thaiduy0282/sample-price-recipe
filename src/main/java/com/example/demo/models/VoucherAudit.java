package com.example.demo.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VoucherAudit extends BaseEntity {

    // Getters and Setters
    private Long id; // Primary key

    private String voucherCode;

    private Long startDate;

    private Long endDate;

    private String lineItemId;

    private Long recipeId;

    private Long accountId;

    private Long productConfigurationId;

}

