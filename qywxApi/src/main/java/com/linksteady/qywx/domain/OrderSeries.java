package com.linksteady.qywx.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderSeries implements Serializable{

    private Long userId;

    private Date orderDt;

    private String orderDtStr;

    private String spuName;

    private String productName;

    private Double orderFee;

    private Integer intervalDays;
}
