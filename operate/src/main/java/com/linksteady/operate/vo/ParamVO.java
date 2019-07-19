package com.linksteady.operate.vo;

import org.apache.commons.lang3.StringUtils;

/**
 * @author hxcao
 * @date 2019-06-15
 */
public class ParamVO {

    private String periodType;

    private String startDt;

    private String endDt;

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public String getStartDt() {
        if(StringUtils.isNotBlank(startDt)) {
            startDt = startDt.replaceAll("-", "");
        }
        return startDt;
    }

    public void setStartDt(String startDt) {
        this.startDt = startDt;
    }

    public String getEndDt() {
        if(StringUtils.isNotBlank(endDt)) {
            endDt = endDt.replaceAll("-", "");
        }
        return endDt;
    }

    public void setEndDt(String endDt) {
        this.endDt = endDt;
    }
}
