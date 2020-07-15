package com.linksteady.operate.vo;

import lombok.Data;

@Data
public class SmsStatisVO {

    private String smsContent;

    private int cnt;

    private int smsLength;

    private int smsBillingCount;
}
