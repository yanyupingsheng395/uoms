package com.linksteady.common.domain;

/**
 * 日志类型
 * @author huang
 * @date
 */
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
