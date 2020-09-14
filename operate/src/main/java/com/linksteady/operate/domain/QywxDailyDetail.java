package com.linksteady.operate.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author huang
 * @date 2020-05-15
 */
@Data
public class QywxDailyDetail implements Serializable {

    /**
     * ID
     */
    private Long detailId;

    /**
     * 头表ID
     */
    private Long headId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活跃度
     */
    private String pathActive;

    /**
     * 用户价值
     */
    private String userValue;

    /**
     * 成长SPU
     */
    private String spuName;

    /**
     * 件单价
     */
    private double piecePrice;

    /**
     * 连带率
     */
    private String joinRate;

    /**
     * 建议触达时段
     */
    private String orderPeriod;

    /**
     * 完成购买
     */
    private String completePurch;

    /**
     * 优惠券ID
     */
    private String couponId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 优惠券领取URL
     */
    private String couponUrl;

    /**
     * 所在群组
     */
    private String groupName;

    /**
     * 是否触达
     */
    private String isPush;

    /**
     * 是否已转化
     */
    private String isConversion;

    /**
     * 是否核销
     */
    private String isCancle;

    private String pushStatus;

    private String textContent;

    private String actOrderPrice;

    private String userPhone;

    private String userOpenid;

    private String tarProductNum;

    /**
     * 优惠券门槛
     */
    private String couponMin;

    /**
     * 优惠券面额
     */
    private Double couponDeno;

    private String tarOrderPrice;

    private String recProdId;

    private String recProdName;

    private int isCoupon;

    private String groupId;

    /**
     * 对应企业微信客户ID
     */
    private String qywxContractId;

    /**
     * 对应企业微信成员ID
     */
    private String qywxUserId;

    /**
     * 对应企业微信成员名称
     */
    private String qywxUserName;

    /**
     * 用户生命周期 值参考数据字典 默认为0
     */
    private String lifecycle;
}
