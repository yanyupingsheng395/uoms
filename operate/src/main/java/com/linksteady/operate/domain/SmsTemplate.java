package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Data
@Table(name = "UO_OP_SMS_TEMPLATE")
public class SmsTemplate {

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createDt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date updateDt;

    @Transient
    private Integer usedDays;

    /**
     * 文案被引用次数
     */
    @Transient
    private int refCnt;

    /**
     * 是否当前组正在使用的文案 (仅在每日用户成长配置那使用)
     */
    @Transient
    private String currentFlag;
    
    @Transient
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date opDt;
}