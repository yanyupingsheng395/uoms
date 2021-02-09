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

    /**
     * 券标记，券的批次码
     */
    private String couponIdentity;
    /**
     * 券名称
     */
    private String couponName;
    /**
     * 券有效开始时间
     */
    private LocalDate beginDate;
    /**
     * 券有效结束时间
     */
    private LocalDate endDate;
}
