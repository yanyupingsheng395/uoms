package com.linksteady.operate.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

@Table(name = "UO_REASON_RESULT")
public class ReasonResult {
    @Column(name = "REASON_ID")
    private BigDecimal reasonId;

    @Column(name = "FCODE")
    private String fcode;

    @Column(name = "FNAME")
    private String fname;

    @Column(name = "FORMULA")
    private String formula;

    @Column(name = "BUSINESS")
    private String business;

    @Column(name = "F_ORDER_NO")
    private BigDecimal fOrderNo;

    @Column(name = "CREATE_DT")
    private Date createDt;

    /**
     * @return REASON_ID
     */
    public BigDecimal getReasonId() {
        return reasonId;
    }

    /**
     * @param reasonId
     */
    public void setReasonId(BigDecimal reasonId) {
        this.reasonId = reasonId;
    }

    /**
     * @return FCODE
     */
    public String getFcode() {
        return fcode;
    }

    /**
     * @param fcode
     */
    public void setFcode(String fcode) {
        this.fcode = fcode == null ? null : fcode.trim();
    }

    /**
     * @return FNAME
     */
    public String getFname() {
        return fname;
    }

    /**
     * @param fname
     */
    public void setFname(String fname) {
        this.fname = fname == null ? null : fname.trim();
    }

    /**
     * @return FORMULA
     */
    public String getFormula() {
        return formula;
    }

    /**
     * @param formula
     */
    public void setFormula(String formula) {
        this.formula = formula == null ? null : formula.trim();
    }

    /**
     * @return BUSINESS
     */
    public String getBusiness() {
        return business;
    }

    /**
     * @param business
     */
    public void setBusiness(String business) {
        this.business = business == null ? null : business.trim();
    }

    /**
     * @return F_ORDER_NO
     */
    public BigDecimal getfOrderNo() {
        return fOrderNo;
    }

    /**
     * @param fOrderNo
     */
    public void setfOrderNo(BigDecimal fOrderNo) {
        this.fOrderNo = fOrderNo;
    }

    /**
     * @return CREATE_DT
     */
    public Date getCreateDt() {
        return createDt;
    }

    /**
     * @param createDt
     */
    public void setCreateDt(Date createDt) {
        this.createDt = createDt;
    }
}