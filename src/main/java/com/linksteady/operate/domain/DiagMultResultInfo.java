package com.linksteady.operate.domain;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 诊断 乘法的返回结果集
 * @author  huang
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DiagMultResultInfo  extends DiagResultInfo {

    /**
     *对应横轴  时间
     */
    List<String> xData;

    /**
     *x轴的名称
     */
    String xName;

    /**
     *第1个图的y轴名称
     */
    String firYName;

    /**
     *第1个图的数据
     */
    List<Double> firData;

    /**
     *第1个图的变化率
     */
    double firChangeRate;

    /**
     *第1个平均值
     */
    double firAvg;

    /**
     *上浮5%值
     */
    double firUp;

    /**
     *下浮5%值
     */
    double firDown;

    /**
     * 第2个图的y轴名称
     */
    String secYName;

    /**
     *第2个图的数据
     */
    List<Double> secData;

    /**
     *第2个图的变化率
     */
    double secChangeRate;

    /**
     *第2个平均值
     */
    double secAvg;

    /**
     *上浮5%值
     */
    double secUp;

    /**
     *下浮5%值
     */
    double secDown;

    /**
     *第3个图的y轴名称
     */
    String thirdYName;

    /**
     *第3个图的数据
     */
    List<Double> thirdData;

    /**
     *第3个图的变化率
     */
    double thirdChangeRate;

    /**
     *第3个平均值
     */
    double thirdAvg;

    /**
     *上浮5%值
     */
    double thirdUp;

    /**
     *下浮5%值
     */
    double thirdDown;

    /**
     *变异系数  k为名称 v为对应的变异系数值
     */
    Map<String, Double> covValues;

    /**
     *变异系数编码、变异系数名称 编码为cov1 cov2 cov3
     */
    //Map<String, String> covNames;  废弃

    /**
     *相关性
     */
    JSONObject relate;

}
