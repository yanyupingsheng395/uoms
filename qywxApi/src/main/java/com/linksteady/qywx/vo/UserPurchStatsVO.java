package com.linksteady.qywx.vo;

import lombok.Data;

@Data
public class UserPurchStatsVO implements java.io.Serializable{

    private double purchAmout;
    private double purchAmountLevel;
    private double purchPrice;
    private double purchPriceLevel;
    private int purchTimes;
    private double purchTimesLevel;
    private double purchInterval;
    private double purchIntervalLevel;
    private double discountPct;
    private double discountPctLevel;
    private double avgDiscount;
    private double avgDiscountLevel;
}
