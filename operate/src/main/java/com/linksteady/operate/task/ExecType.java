package com.linksteady.operate.task;

public enum ExecType {
    EFFECT_DAILY_KEY("daily","每日运营效果计算"),
    EFFECT_ACTIVITY_KEY("activity","活动运营效果计算"),
    EFFECT_MANUAL_KEY("manual","手工推送效果计算");

    private String key;
    private String desc;

    ExecType(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 根据获取其注释信息
     * @param key
     * @return
     */
    public String getDescByKey(String key)
    {
        for (ExecType c : ExecType.values()) {
            if (c.getKey().equals(key)) {
                return c.getDesc();
            }
        }
        return "";
    }
}
