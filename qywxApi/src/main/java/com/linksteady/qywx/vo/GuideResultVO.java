package com.linksteady.qywx.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class GuideResultVO implements Serializable {

    /**
     * 用户总数
     */
    private Integer totalCnt;

    /**
     * 添加用户数
     */
    private Integer addCnt;

    /**
     * 购买用户数
     */
    private Integer purchCnt;

    /**
     * 用户购买率
     */
    private double purchRate;

    /**
     * 购买金额
     */
    private double totalAmount;


    /**
     * 接收消息条数
     */
    private Integer receiveMsg;

    /**
     * 推送消息条数
     */
    private Integer pushMsg;

    /**
     * 消息推送执行率
     */
    private double pushExecuteRate;

    /**
     * 消息任务对应推送用户数
     */
    private Integer receiveUserCnt;

    /**
     * 推送用户数
     */
    private Integer pushUserCnt;

    /**
     * 推送购买用户数
     */
    private Integer pushCovCnt;

    /**
     * 推送转化率
     */
    private double pushCovRate;

    /**
     * 累计推送购买金额
     */
    private double pushTotalAmount;

    /**
     * 订单金额
     */
    private double orderAmount;

    /**
     * 占总体
     */
    private double pushAmountPct;


}
