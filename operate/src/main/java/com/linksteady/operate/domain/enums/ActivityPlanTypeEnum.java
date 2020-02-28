package com.linksteady.operate.domain.enums;

public enum ActivityPlanTypeEnum {

    Notify("NOTIFY","活动提醒"),During("DURING","活动期间");

    private String planTypeCode;

    private String getPlanTypeName;

    ActivityPlanTypeEnum(String planTypeCode, String getPlanTypeName) {
        this.planTypeCode = planTypeCode;
        this.getPlanTypeName = getPlanTypeName;
    }

    public String getPlanTypeCode() {
        return planTypeCode;
    }

    public void setPlanTypeCode(String planTypeCode) {
        this.planTypeCode = planTypeCode;
    }

    public String getGetPlanTypeName() {
        return getPlanTypeName;
    }

    public void setGetPlanTypeName(String getPlanTypeName) {
        this.getPlanTypeName = getPlanTypeName;
    }

    // 普通方法
    public static String getPlanTypeName(String planTypeCode) {
        for (ActivityPlanTypeEnum c : ActivityPlanTypeEnum.values()) {
            if (c.getPlanTypeCode().equals(planTypeCode)) {
                return c.getPlanTypeName;
            }
        }
        return "";
    }
}
