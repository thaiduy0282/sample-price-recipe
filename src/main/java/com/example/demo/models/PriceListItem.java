package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

//@CosmosUniqueKeyPolicy(uniqueKeys = {@CosmosUniqueKey(paths = {"/name"})})
public class PriceListItem {

    private String currencyIsoCode;
    private String currencyId;
    public boolean isStandard;
    private String accountId;
    private String accountNumber;
    private String accountName;

    private String scope;
    public boolean isSearchable;
    
    private String priceListScopeId;
}