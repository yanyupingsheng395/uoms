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
     * 目标分类
     */
    private String tarType;

    /**
     * 活跃度
     */
    private String pathActiv;

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
    private String piecePrice;

    /**
     * 连带率
     */
    private String joinRate;

    /**
     * 购买品类种数
     */
    private String purchTypeNum;

    /**
     * 建议触达时段
     */
    private String orderPeriod;

    /**
     * 建议优惠面额
     */
    private String referDeno;

    /**
     * 建议折扣优惠力度
     */
    private String discountLevel;

    /**
     * 购买商品名称
     */
    private String purchProductName;

    /**
     * 购买商品次数
     */
    private String purchTimes;

    /**
     * 订单价
     */
    private String orderPrice;

    /**
     * 完成购买
     */
    private String completePurch;

    /**
     * 紧迫度
     */
    private String urgencyLevel;

    /**
     * 留存推荐商品名称
     */
    private String recRetentionName;

    /**
     * 向上推荐商品名称
     */
    private String recUpName;

    /**
     * 交叉推荐商品名称
     */
    private String recCrossName;

    /**
     * 最终推荐商品
     */
    private String recLastName;

    /**
     * 优惠券ID
     */
    private String couponId;

    /**
     * 策略模板
     */
    private Long growthStrategyId;

    /**
     * 组ID
     */
    private Long groupId;

    /**
     * 优惠券大小
     */
    private String couponDenom;

    /**
     * 推荐类型
     */
    private String recType;
}
