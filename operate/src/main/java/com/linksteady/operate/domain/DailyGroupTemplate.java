package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-09-20
 */
@Data
public class DailyGroupTemplate {

    private String groupId;

    private String groupName;

    private String smsCode;

    private String smsContent;

    /**
     * 是否包含优惠券
     */
    private String isCoupon;

    /**
     * N个优惠券的名称
     */
    private String couponName;

    /**
     * N个优惠券的ID
     */
    private String couponId;

    /**
     * 用户价值
     */
    private String userValue;

    /**
     * 活跃度
     */
    private String pathActive;

    /**
     * 生命周期
     */
    private String lifecycle;

    private String checkFlag;

    private String checkComments;

    private String groupInfo;

    /**
     * 目标分类  (用户成长类目的特性 H表示高利润 L表示低利润)
     */
    private String tarType;

    /**
     * 公众号消息ID
     */
    private String wxpubId;

    /**
     * 企业微信消息ID
     */
    private String qywxId;

    private String smsOpFlag;

    private String qywxOpFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date smsOpDt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date qywxOpDt;
}
