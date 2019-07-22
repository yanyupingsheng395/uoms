package com.linksteady.operate.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date timeToMarket;


    private BigDecimal stockbysales;


    private BigDecimal onsaleDuration;


    private Date saleBeginDt;


    private Date saleEndDt;

    private Double gmvCont;

    private Integer orderNo;

    private Double newUserCont;

    private Double oldUserCont;

    private Double poCount;

    private Double joinrate;

    private Double sprice;

}
