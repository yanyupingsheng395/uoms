package com.linksteady.operate.domain.enums;

/**
 * 活动运营 - 阶段的枚举类
 *
 * @author huang
 * @date
 */
public enum  ActivityStageEnum {

    preheat("preheat","预售"),formal("formal","正式");

    private String stageCode;

    private String stageDesc;

    ActivityStageEnum(String stageCode, String stageDesc) {
        this.stageCode = stageCode;
        this.stageDesc = stageDesc;
    }

    public String getStageCode() {
        return stageCode;
    }

    public void setStageCode(String stageCode) {
        this.stageCode = stageCode;
    }

    public String getStageDesc() {
        return stageDesc;
    }

    public void setStageDesc(String stageDesc) {
        this.stageDesc = stageDesc;
    }
}
