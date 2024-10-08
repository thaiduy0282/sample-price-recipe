package com.example.demo.models;

import ai.qworks.dao.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.Objects;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@Entity
@Table(
        name = "voucheraudit",
        comment = "VoucherAudit"
)
public class VoucherAudit extends BaseEntity{

    private static final long serialVersionUID = 1226448799579277351L;

    @Column(
            name = "voucherCode"
    )
    private String voucherCode;

    @Column(
            name = "startDate"
    )
    private Long startDate;

    @Column(
            name = "endDate"
    )
    private Long endDate;

    @Column(
            name = "lineItemId"
    )
    private String lineItemId;

    @Column(
            name = "recipeId"
    )
    private Long recipeId;

    @Column(
            name = "accountId"
    )
    private String accountId;

    @Column(
            name = "productConfigurationId"
    )
    private String productConfigurationId;

    public VoucherAudit() {}

    public VoucherAudit(String voucherCode, Long startDate, Long endDate, String lineItemId, Long recipeId, String accountId, String productConfigurationId) {
        this.voucherCode = voucherCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lineItemId = lineItemId;
        this.recipeId = recipeId;
        this.accountId = accountId;
        this.productConfigurationId = productConfigurationId;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public String getLineItemId() {
        return lineItemId;
    }

    public void setLineItemId(String lineItemId) {
        this.lineItemId = lineItemId;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getProductConfigurationId() {
        return productConfigurationId;
    }

    public void setProductConfigurationId(String productConfigurationId) {
        this.productConfigurationId = productConfigurationId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VoucherAudit that = (VoucherAudit) o;
        return Objects.equals(voucherCode, that.voucherCode) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate) && Objects.equals(lineItemId, that.lineItemId) && Objects.equals(recipeId, that.recipeId) && Objects.equals(accountId, that.accountId) && Objects.equals(productConfigurationId, that.productConfigurationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), voucherCode, startDate, endDate, lineItemId, recipeId, accountId, productConfigurationId);
    }
}

