package com.linksteady.operate.vo;

import lombok.Data;

@Data
public class DiagConditionNew {
    /**
     * 维度编码
     */
    private String dimCode;

    /**
     * 维度名称
     */
    private String dimName;

    /**
     * 维度值
     */
    private String dimValues;

    /**
     * 维度显示值
     */
    private String dimValueDisplay;

    /**
     * 是否从父节点继承的标志
     */
    private String inheritFlag;
}
