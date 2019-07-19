package com.linksteady.mdss.vo;

import lombok.Data;

@Data
public class DimJoinVO {

    private String dimTable;

    private String relation;

    private String dimTableAlias;

    private String dimWhere;

    private String dimWhereType;

    private String dirverTableName;

    private String dimCode;
}
