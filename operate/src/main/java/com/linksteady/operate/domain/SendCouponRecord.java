package com.linksteady.operate.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SendCouponRecord {

    private Long sendRecordId;

    private Long headId;

    private String couponInfo;

    private String userInfo;

    private String sendResult;

    private String sendResultDesc;

    private LocalDateTime insertDt;


}
