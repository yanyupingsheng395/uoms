package com.linksteady.qywx.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/9/8
 */
@Data
public class QywxWelcomeCoupon {
    private Integer couponId;
    private String couponCode;
    private Double couponDeno;
    private Double couponThreshold;
    private String endDate;
    private String couponUrl;
    private String couponScene;
    private String checkFlag;
    private String checkComment;
}
