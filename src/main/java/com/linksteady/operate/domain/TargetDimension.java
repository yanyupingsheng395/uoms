package com.linksteady.operate.domain;

import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "UO_TGT_DIMENSION")
public class TargetDimension {
    /**
     * ID
     */
    @Column(name = "ID")
    private Long id;

    /**
     * 目标D
     */
    @Column(name = "TGT_ID")
    private Long tgtId;

    /**
     * 维度编码
     */
    @Column(name = "DIMENSION_CODE")
    private String dimensionCode;

    /**
     * 维度名称
     */
    @Column(name = "DIMENSION_NAME")
    private String dimensionName;

    /**
     * 维度值编码
     */
    @Column(name = "DIMENSION_VAL_CODE")
    private String dimensionValCode;

    /**
     * 维度值名称
     */
    @Column(name = "DIMENSION_VAL_NAME")
    private String dimensionValName;
}