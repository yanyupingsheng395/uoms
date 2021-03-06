package com.linksteady.operate.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-07-31
 */
@Data
public class DailyDetail {

    /**
     * ID
     */
    private Long dailyDetailId;

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
     * 实际购买商品名称
     */
    private String actProdName;

    /**
     * 实际件单价
     */
    private String actPiecePrice;

    /**
     * 是否核销
     */
    private String isCancle;

    private String pushStatus;

    private String smsContent;

    private String actOrderPerice;

    private String userPhone;

    private String userOpenid;

    private String tarProductNum;

    /**
     * 优惠券门槛
     */
    private double couponMin;

    /**
     * 优惠券面额
     */
    private double couponDeno;

    private String tarOrderPrice;

    private String recProdId;

    private String recProdName;

    private int isCoupon;

    private String groupId;

    /**
     * 短信文案计费条数
     */
    private Integer smsBillingCount;

}
