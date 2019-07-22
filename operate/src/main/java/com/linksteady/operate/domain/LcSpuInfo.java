package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LcSpuInfo {

    private Long spuWid;

    private String spuName;

    private BigDecimal stockNum;

    private BigDecimal salesNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date timeToMarket;

    private BigDecimal stockbysales;

    private BigDecimal onsaleDuration;

    private Date saleBeginDt;

    private Date saleEndDt;

    private Double gmvTotal;

    private Integer poCount;

    private Double joinrate;

    private Double sprice;

    private Integer newuser;

    private  Integer olduser;

}