package com.linksteady.operate.domain.enums;

public enum  PushPropertiesEnum {
    repeatPushDays("repeatPushDays","op.push.repeatPushDays"),

    pushFlag("pushFlag","op.push.pushFlag"),
    pushMethod("pushMethod","op.push.pushMethod"),
    smsLengthLimit("smsLengthLimit","op.push.smsLengthLimit"),
    productUrl("productUrl","op.push.productUrl"),
    isTestEnv("isTestEnv","op.push.isTestEnv"),

    demoShortUrl("demoShortUrl","op.push.demoShortUrl"),
    shortUrlLen("shortUrlLen","op.push.shortUrlLen"),

    prodNameLen("prodNameLen","op.push.prodNameLen"),
    couponSendType("couponSendType","op.push.couponSendType"),
    couponNameLen("couponNameLen","op.push.couponNameLen"),
    priceLen("priceLen","op.push.priceLen"),


    pushVendor("pushVendor","op.push.pushVendor"),

    openNightSleep("openNightSleep","op.push.openNightSleep"),
    nightStart("nightStart","op.push.nightStart"),
    nightEnd("nightEnd","op.push.nightEnd"),
    OpenCallback("OpenCallback","op.push.OpenCallback"),

    clAccount("clAccount","op.push.clAccount"),
    clPassword("clPassword","op.push.clPassword"),
    clRequestServerUrl("clRequestServerUrl","op.push.clRequestServerUrl"),
    clPullMoUrl("clPullMoUrl","op.push.clPullMoUrl"),
    clReportRequestUrl("clReportRequestUrl","op.push.clReportRequestUrl"),


    montnetsAccount("montnetsAccount","op.push.montnetsAccount"),
    montnetsPassword("montnetsPassword","op.push.montnetsPassword"),
    montnetsMasterIpAddress("montnetsMasterIpAddress","op.push.montnetsMasterIpAddress");

    private String fieldName;

    private String keyCode;


    PushPropertiesEnum(String fieldName, String keyCode) {
        this.fieldName = fieldName;
        this.keyCode = keyCode;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }
}

