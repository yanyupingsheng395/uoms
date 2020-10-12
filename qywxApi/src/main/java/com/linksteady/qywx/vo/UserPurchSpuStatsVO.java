package com.linksteady.qywx.vo;

import lombok.Data;

@Data
public class UserPurchSpuStatsVO implements java.io.Serializable{

    private double purchAmout;
    private double purchAmountLevel;

    private double purchAmoutPct;
    private double purchAmoutPctLevel;

    private String prodName;
    private double prodNamePct;

    private String expensiveProdName;
    private double expensiveProdNamePct;

    private double discountPct;
    private double discountPctLevel;
    private double avgDiscount;
    private double avgDiscountLevel;
}
