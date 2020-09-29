package com.linksteady.operate.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 企业微信进行发券时的包装对象
 */
@Data
public class CouponInfoVO implements Serializable {

    private Long couponId;

    private String couponIdentity;

    private String couponName;

    private LocalDate beginDate;

    private LocalDate endDate;
}
