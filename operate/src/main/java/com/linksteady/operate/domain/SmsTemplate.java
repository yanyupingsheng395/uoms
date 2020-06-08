package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

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
     * 补贴链接
     */
    @Column(name = "IS_COUPON_URL")
    private String isCouponUrl;

    /**
     * 补贴名称
     */
    @Column(name = "IS_COUPON_NAME")
    private String isCouponName;

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

    private String userValue;
    private String lifeCycle;
    private String pathActive;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createDt;
    private Date updateDt;
    private Integer usedDays;
}