package com.linksteady.common.domain;

public enum LogTypeEnum {
    PAGE("页面跳转类型"),AJAX("ajax请求");

    private String desc;

    LogTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
