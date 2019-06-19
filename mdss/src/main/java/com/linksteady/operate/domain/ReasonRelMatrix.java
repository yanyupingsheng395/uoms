package com.linksteady.operate.domain;

import java.util.Date;
import javax.persistence.*;

@Table(name = "UO_REASON_REL_MATRIX")
public class ReasonRelMatrix {
    /**
     * 诊断ID
     */
    @Column(name = "REASON_ID")
    private Long reasonId;
    /**
     * 因子CODE
     */
    @Column(name = "F_CODE")
    private String fCode;

    /**
     * 因子名称
     */
    @Column(name = "F_NAME")
    private String fName;

    /**
     * 相关因子CODE
     */
    @Column(name = "RF_CODE")
    private String rfCode;

    /**
     * 相关因子名称
     */
    @Column(name = "RF_NAME")
    private String rfName;

    /**
     * 相关系数
     */
    @Column(name = "RELATE_VALUE")
    private Double relateValue;

    /**
     * 创建日期
     */
    @Column(name = "CREATE_DT")
    private Date createDt;

    /**
     * 因子排序号
     */
    @Column(name = "F_ORDERNO")
    private Long fOrderno;

    /**
     * 相关因子排序号
     */
    @Column(name = "RF_ORDER_NO")
    private Long rfOrderNo;

    public Long getReasonId() {
        return reasonId;
    }

    public void setReasonId(Long reasonId) {
        this.reasonId = reasonId;
    }

    public String getfCode() {
        return fCode;
    }

    public void setfCode(String fCode) {
        this.fCode = fCode;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getRfCode() {
        return rfCode;
    }

    public void setRfCode(String rfCode) {
        this.rfCode = rfCode;
    }

    public String getRfName() {
        return rfName;
    }

    public void setRfName(String rfName) {
        this.rfName = rfName;
    }

    public Double getRelateValue() {
        return relateValue;
    }

    public void setRelateValue(Double relateValue) {
        this.relateValue = relateValue;
    }

    public Date getCreateDt() {
        return createDt;
    }

    public void setCreateDt(Date createDt) {
        this.createDt = createDt;
    }

    public Long getfOrderno() {
        return fOrderno;
    }

    public void setfOrderno(Long fOrderno) {
        this.fOrderno = fOrderno;
    }

    public Long getRfOrderNo() {
        return rfOrderNo;
    }

    public void setRfOrderNo(Long rfOrderNo) {
        this.rfOrderNo = rfOrderNo;
    }
}