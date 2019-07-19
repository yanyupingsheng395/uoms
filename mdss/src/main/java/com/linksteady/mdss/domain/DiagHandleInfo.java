package com.linksteady.mdss.domain;

import com.linksteady.mdss.vo.DiagConditionVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 诊断操作描述的表
 * @author  huang
 */
@Data
public class DiagHandleInfo implements Serializable {

    /**
     * 诊断ID
     */
    int diagId;

    /**
     * 等级ID
     */
    int kpiLevelId;

    /**
     * 诊断周期类型
     */
    String periodType;

    /**
     * 开始时间
     */
    String beginDt;

    /**
     *结束时间
     */
    String endDt;

    /**
     *操作描述 gmv按XXX做加法分析； GMV 按 XX*XX 拆分；
     */
    String handleDesc;

    /**
     *标签名称 预留字段 暂时不用
     */
    String tabName;

    /**
     *操作类型  M乘法 A加法 F过滤(无操作)
     */
    String handleType;

    /**
     *模板名称
     */
    String templateName;

    /**
     *节点ID 在那个节点上进行的新增操作，就存那个节点的ID
     */
    String nodeId;

    /**
     *乘法公式的乘数1
     */
    String formula1;

    /**
     *乘法公式的乘数2
     */
    String formula2;

    /**
     *指标编码 (也就是拆分的那个指标)
     */
    String kpiCode;

    /**
     *加法按那个维度进行拆分
     */
    String addDimCode;

    /**
     *加法拆分的维度值
     */
    String addDimValues;

    /**
     * 条件信息 list中每一条记录：select t.dim_code,t.dim_name,t.dim_values,t.dim_value_display,t.inherit_flag from UO_DIAG_CONDITION t
     */
    List<DiagConditionVO> whereinfo;

    /**
    *主指标编码（根节点对应的编码）
    */
    String mainKpiCode;

}
