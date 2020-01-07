package com.linksteady.operate.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@Table(name = "UO_OP_SMS_TEMPLATE")
public class SmsTemplate {

    private String smsName;

    /**
     * 短信模板编号
     */
    @Column(name = "SMS_CODE")
    private String smsCode;

    /**
     * 短信模板内容
     */
    @Column(name = "SMS_CONTENT")
    private String smsContent;

    /**
     * 体现补贴链接与名称
     */
    @Column(name = "IS_COUPON")
    private String isCoupon;

    /**
     * 是否包含商品名
     */
    private String isProductName;

    /**
     * 是否包含商品详情页
     */
    private String isProductUrl;

    /**
     * 备注
     */
    private String remark;
}