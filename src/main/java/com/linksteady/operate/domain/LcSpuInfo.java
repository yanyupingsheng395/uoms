package com.linksteady.operate.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "UO_LC_SPU_LIST")
public class LcSpuInfo {
    @Column(name = "SPU_WID")
    private Long spuWid;

    @Column(name = "SPU_NAME")
    private String spuName;

    @Column(name = "STOCK_NUM")
    private BigDecimal stockNum;

    @Column(name = "SALES_NUM")
    private BigDecimal salesNum;

    @Column(name = "TIME_TO_MARKET")
    private Date timeToMarket;

    @Column(name = "STOCKBYSALES")
    private BigDecimal stockbysales;

    @Column(name = "ONSALE_DURATION")
    private BigDecimal onsaleDuration;

    @Column(name = "SALE_BEGIN_DT")
    private Date saleBeginDt;

    @Column(name = "SALE_END_DT")
    private Date saleEndDt;

    @Column(name = "REAL_FEE")
    private Double gmvTotal;

    @Column(name = "GMV_RELATE")
    private BigDecimal gmvRelate;
}