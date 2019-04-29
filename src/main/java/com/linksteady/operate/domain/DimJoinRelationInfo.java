package com.linksteady.operate.domain;

import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "UO_DIM_JOINRELATION_CONFIG")
public class DimJoinRelationInfo {
    @Column(name = "DIRVER_TABLE_NAME")
    private String dirverTableName;

    @Column(name = "DIM_CODE")
    private String dimCode;

    @Column(name = "DIM_TABLE")
    private String dimTable;

    @Column(name = "RELATION")
    private String relation;

    @Column(name = "DIM_TABLE_ALIAS")
    private String dimTableAlias;

    @Column(name = "DIM_WHERE")
    private String dimWhere;

    @Column(name = "DIM_WHERE_TYPE")
    private String dimWhereType;
}