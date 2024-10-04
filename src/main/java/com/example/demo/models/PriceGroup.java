package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class PriceGroup {
	private static final long serialVersionUID = 8204041143290681328L;
	private String name;
	private int sequence;
}
