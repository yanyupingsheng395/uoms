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
    List<String> firData;

    /**
     *第1个图的变化率
     */
    String firChangeRate;

    /**
     *第1个平均值
     */
    String firAvg;

    /**
     *上浮5%值
     */
    String firUp;

    /**
     *下浮5%值
     */
    String firDown;

    /**
     * 第2个图的y轴名称
     */
    String secYName;

    /**
     *第2个图的数据
     */
    List<String> secData;

    /**
     *第2个图的变化率
     */
    String secChangeRate;

    /**
     *第2个平均值
     */
    String secAvg;

    /**
     *上浮5%值
     */
    String secUp;

    /**
     *下浮5%值
     */
    String secDown;

    /**
     *第3个图的y轴名称
     */
    String thirdYName;

    /**
     *第3个图的数据
     */
    List<String> thirdData;

    /**
     *第3个图的变化率
     */
    String thirdChangeRate;

    /**
     *第3个平均值
     */
    String thirdAvg;

    /**
     *上浮5%值
     */
    String thirdUp;

    /**
     *下浮5%值
     */
    String thirdDown;

    /**
     *变异系数  k为名称 v为对应的变异系数值
     */
    Map<String, String> covValues;

    /**
     *相关性
     */
    JSONObject relate;

}
