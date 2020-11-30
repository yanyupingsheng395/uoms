package com.linksteady.qywx.domain;

import lombok.Data;

@Data
public class UserBuyHistory implements java.io.Serializable{

    private String userId;

    private String orderDt;

    private Long spuId;

    private String spuName;

    private String productName;

    private Double orderFee;

    private Long intervalDays;
}
