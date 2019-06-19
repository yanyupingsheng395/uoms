package com.linksteady.operate.vo;

import lombok.Data;

import java.util.Date;

/**
 * 诊断 - 条件VO
 * @author huang
 */
@Data
public class DiagConditionVO {

    /**
     * 诊断ID
     */
    private Long diagId;

    /**
     * 节点ID
     */
    private Long nodeId;

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

    /**
     * 创建时间
     */
    private Date createDt;
}
