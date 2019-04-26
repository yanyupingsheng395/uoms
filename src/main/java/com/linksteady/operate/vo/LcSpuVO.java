package com.linksteady.operate.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LcSpuVO {

    private BigDecimal spuWid;


    private String spuName;


    private BigDecimal stockNum;


    private BigDecimal salesNum;


    private Date timeToMarket;


    private BigDecimal stockbysales;


    private BigDecimal onsaleDuration;


    private Date saleBeginDt;


    private Date saleEndDt;

    private Double gmvCont;

    private Double gmvRelate;

    private int orderNo;
}
