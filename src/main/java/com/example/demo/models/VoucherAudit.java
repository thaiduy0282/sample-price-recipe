package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "voucher_audit")
public class VoucherAudit extends BaseEntity {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @Column(name = "voucher_code", nullable = false)
    private String voucherCode;

    @Column(name = "start_date", nullable = false)
    private Long startDate;

    @Column(name = "end_date", nullable = false)
    private Long endDate;

    @Column(name = "line_item_id", nullable = false)
    private String lineItemId;

    @Column(name = "recipe_id", nullable = false)
    private Long recipeId;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "product_configuration_id", nullable = false)
    private Long productConfigurationId;

}

