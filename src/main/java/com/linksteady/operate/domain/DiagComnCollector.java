package com.linksteady.operate.domain;

import lombok.Data;

/**
 * 此类用来接收通过查询接口返回的数据，为了防止map返回的数据类型转换引起的异常，所以构造了此类
 * @author huang
 */
@Data
public class DiagComnCollector {

    /**
     * 周期名称
     */
    public String periodName;

    /**
     * 交易总金额
     */
    public Double gmv;

    /**
     * 交易用户数
     */
    public Double ucnt;

    /**
     * 客单价
     */
    public Double uprice;

    /**
     *订单价
     */
    public Double price;

    /**
     * 订单数
     */
    public Double pcnt;

    /**
     * 连带率
     */
    public Double joinrate;

    /**
     * 件单价
     */
    public Double sprice;

    /**
     * 吊牌价
     */
    public Double sprice2;

    /**
     * 折扣率
     */
    public Double disrate;

    /**
     * 时间长度
     */
    public Double tspan;

    /**
     * 购买频率
     */
    public Double freq;

}
