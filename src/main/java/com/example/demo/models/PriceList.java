package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;


@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)

public class PriceList {

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