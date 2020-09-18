package com.linksteady.qywx.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hxcao
 * @date 2020/9/17
 */
@Data
public class OperateResult implements Serializable {

    /**
     * 用户总数
     */
    private Integer userTotalCnt;

    /**
     * 添加用户数
     */
    private Integer addUserCnt;

    /**
     * 购买用户数
     */
    private Integer purchasedUserCnt;

    /**
     * 用户购买率
     */
    private double purchasedRate;

    /**
     * 累计购买金额
     */
    private double totalPurchasedAmount;

    /**
     * 观测期购买金额
     */
    private double observePurchasedAmount;

    /**
     * 接收消息条数
     */
    private Integer receiveMsgCnt;

    /**
     * 推送消息条数
     */
    private Integer pushMsgCnt;

    /**
     * 推送消息执行率
     */
    private double pushExecuteRate;

    /**
     * 消息任务对应推送用户数
     */
    private Integer pushMsgUserCnt;

    /**
     * 推送用户数
     */
    private Integer pushUserCnt;

    /**
     * 推送购买用户数
     */
    private Integer pushAndPurchaseUserCnt;

    /**
     * 推送转化率
     */
    private double pushConvertRate;

    /**
     * 累计推送购买金额
     */
    private double allPushAmount;

    /**
     * 占整体
     */
    private double allPushAmountScale;


}
