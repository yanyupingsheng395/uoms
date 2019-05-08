package com.linksteady.operate.domain;

import java.math.BigDecimal;
import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "UO_DIM_LIST_CONFIG")
public class DimConfigInfo {
    @Column(name = "DIM_CODE")
    private String dimCode;

    @Column(name = "DIM_NAME")
    private String dimName;

    @Column(name = "VALUE_TYPE")
    private String valueType;

    @Column(name = "VALUE_SQL")
    private String valueSql;

    @Column(name = "IS_ALL")
    private String isAll;

    @Column(name = "REASON_FLAG")
    private String reasonFlag;

    @Column(name = "DIAG_FLAG")
    private String diagFlag;

    @Column(name = "ORDER_NO")
    private BigDecimal orderNo;

    @Column(name = "RELY_ORDERDETAIL")
    private String relyOrderDetail;


}