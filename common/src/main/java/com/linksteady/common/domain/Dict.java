package com.linksteady.common.domain;

import lombok.Data;

import java.util.Date;

@Data
public class Dict{

	private Long dictId;

	private String code;

	private String value;

	private String typeCode;

	private String typeName;

	private Long orderNo;

	private Date createDt;

	private Date updateDate;

	private String createBy;

	private String updateBy;

	private String remark;

}