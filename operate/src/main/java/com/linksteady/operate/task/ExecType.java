package com.linksteady.operate.task;

/**
 * 调度 任务代码的枚举类
 */
public enum ExecType {
    EFFECT_DAILY_KEY("daily","每日运营效果计算"),
    EFFECT_ACTIVITY_KEY("activity","活动运营效果计算"),
    EFFECT_MANUAL_KEY("manual","手工推送效果计算");

    private String code;
    private String desc;

    ExecType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 根据获取其注释信息
     * @param code
     * @return
     */
    public String getDescByCode(String code)
    {
        for (ExecType c : ExecType.values()) {
            if (c.getCode().equals(code)) {
                return c.getDesc();
            }
        }
        return "";
    }
}
