package com.linksteady.mdss.domain;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 加法的返回结果集
 * @author  huang
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DiagAddResultInfo extends DiagResultInfo{

    /**
     * 图例列表
     */
    List<String> legendData;
    /**
     * x轴名称  (所有图公用)
     */
    String xname;

    /**
     * x轴数据(所有图公用)
     */
    List<String> xdata;

    /**
     * 主指标的散点图数据（ 表现趋势）
     */
    JSONArray areaData;

    /**
     * 主指标的 条图数据 （不分时间，仅根据维度 表现大小）
     */
    JSONArray  mainKpiBarData;

    /**
     * 当前指标的散点图数据（表现趋势）
     */
    JSONArray  lineData;

    /**
     * 当前指标的各部分平均数据
     */
    JSONArray  lineAvgData;

    /**
     * 当前指标的 条图数据 （不分时间，仅根据维度 表现大小）
     */
    JSONArray  currKpiBarData;

    /**
     * 变异系数数据
     */
    JSONArray  covData;

    /**
     * 相关性数据
     */
    JSONArray  relateData;

    /**
     * 加法要插入的节点名称
     */
    JSONArray  nodeList;

}
