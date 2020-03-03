package com.linksteady.common.domain;

import lombok.Data;

@Data
public class Dict{

	private Long dictId;

	private String code;

	private String value;

	private String typeCode;

	private String typeName;

	private int orderNo;

}