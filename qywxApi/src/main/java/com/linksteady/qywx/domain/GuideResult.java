package com.linksteady.qywx.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hxcao
 * @date 2020/9/17
 */
@Data
public class GuideResult implements Serializable {

    /**
     * 用户总数
     */
    private Integer userTotalCnt=89;

    /**
     * 添加用户数
     */
    private Integer addUserCnt=89;

    /**
     * 购买用户数
     */
    private Integer purchasedUserCnt=89;

    /**
     * 用户购买率
     */
    private double purchasedRate=32d;

    /**
     * 累计购买金额
     */
    private double totalPurchasedAmount=3;

    /**
     * 观测期购买金额
     */
    private double observePurchasedAmount=52;

    /**
     * 接收消息条数
     */
    private Integer receiveMsgCnt=90;

    /**
     * 推送消息条数
     */
    private Integer pushMsgCnt=27;

    /**
     * 推送消息执行率
     */
    private double pushExecuteRate=64;

    /**
     * 消息任务对应推送用户数
     */
    private Integer pushMsgUserCnt=4;

    /**
     * 推送用户数
     */
    private Integer pushUserCnt=86;

    /**
     * 推送购买用户数
     */
    private Integer pushAndPurchaseUserCnt=18;

    /**
     * 推送转化率
     */
    private double pushConvertRate=21;

    /**
     * 累计推送购买金额
     */
    private double allPushAmount=97;

    /**
     * 占整体
     */
    private double allPushAmountScale=24;


}
