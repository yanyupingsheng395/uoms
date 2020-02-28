package com.linksteady.operate.domain.enums;

public enum ActivityStatusEnum {
    EDIT("edit","待计划"),TODO("todo","待执行"),DOING("doing","执行中"),DONE("done","执行完");

    private String statusCode;
    private String statusName;

    ActivityStatusEnum(String statusCode, String statusName) {
        this.statusCode = statusCode;
        this.statusName = statusName;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
