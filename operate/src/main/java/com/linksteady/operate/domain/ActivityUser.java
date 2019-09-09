package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2019-09-07
 */
@Data
public class ActivityUser {
    private String userId;
    private String spuName;
    private Long planPurch;
    private String recPiecePrice;
    private String orderPeriod;
    private String referDeno;
    private String discountLevel;
    private Double price;
    private Long recProdId;
    private String recProdName;
    private Long recRetentionId;
    private String recRetentionName;
    private Long recUpId;
    private String recUpName;
    private Long recCrossId;
    private String recCrossName;
    private String recType;
    private Date touchDt;
}
