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
    private Integer userTotalCnt=0;

    /**
     * 添加用户数
     */
    private Integer addUserCnt=0;

    /**
     * 购买用户数
     */
    private Integer purchasedUserCnt=0;

    /**
     * 用户购买率
     */
    private double purchasedRate=0d;

    /**
     * 累计购买金额
     */
    private double totalPurchasedAmount=0;

    /**
     * 观测期购买金额
     */
    private double observePurchasedAmount=0;

    /**
     * 接收消息条数
     */
    private Integer receiveMsgCnt=0;

    /**
     * 推送消息条数
     */
    private Integer pushMsgCnt=0;

    /**
     * 推送消息执行率
     */
    private double pushExecuteRate=0;

    /**
     * 消息任务对应推送用户数
     */
    private Integer pushMsgUserCnt=0;

    /**
     * 推送用户数
     */
    private Integer pushUserCnt=0;

    /**
     * 推送购买用户数
     */
    private Integer pushAndPurchaseUserCnt=0;

    /**
     * 推送转化率
     */
    private double pushConvertRate=0;

    /**
     * 累计推送购买金额
     */
    private double allPushAmount=0;

    /**
     * 占整体
     */
    private double allPushAmountScale=0;


}
