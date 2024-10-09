package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class Voucher {
    private String code;
    private Long startDate;
    private Long endDate;
    private Boolean isUsed;
}
