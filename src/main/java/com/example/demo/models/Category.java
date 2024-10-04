package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {
	private static final long serialVersionUID = 4161900597545680943L;

	public Boolean isRootOffering;
	public Boolean isPrimordial;
	public Boolean isAncestor;
	private String rootId;
	private String primordialId;
	private String type;
	private String image;
	public boolean isSearchable;
	private String priceListId;
	private String priceListName;
}