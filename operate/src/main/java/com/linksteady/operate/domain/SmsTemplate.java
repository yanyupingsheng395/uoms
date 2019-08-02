package com.linksteady.operate.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

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
}