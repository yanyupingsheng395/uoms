package com.linksteady.operate.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户运营-生命周期价值-SPU信息列表
 * @author  huang
 */
@Data
public class LcSpuVO {

    private Long spuWid;


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

    private Integer orderNo;

    private Double userCont;

    private Double newUserCont;

    private Double oldUserCont;

    private Double loyaltyCont;

    private Double poCount;

    private Double joinrate;

    private Double sprice;

    private Double profit;
}
