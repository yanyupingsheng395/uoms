package com.linksteady.operate.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
public class LcSpuInfo {

    private Long spuWid;

    private String spuName;

    private BigDecimal stockNum;

    private BigDecimal salesNum;

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