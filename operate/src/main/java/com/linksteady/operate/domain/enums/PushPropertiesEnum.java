package com.linksteady.operate.domain.enums;

/**
 * 推送属性类的属性名及其在T_CONFIG中配置CODE的映射枚举类
 */
public enum  PushPropertiesEnum {
    repeatPushDays("op.push.repeatPushDays"),

    pushFlag("op.push.pushFlag"),
    pushMethod("op.push.pushMethod"),
    smsLengthLimit("op.push.smsLengthLimit"),
    productUrl("op.push.productUrl"),
    isTestEnv("op.push.isTestEnv"),

    demoShortUrl("op.push.demoShortUrl"),
    shortUrlLen("op.push.shortUrlLen"),

    prodNameLen("op.push.prodNameLen"),
    couponSendType("op.push.couponSendType"),
    prodUrlEnabled("op.push.produrl_enabled"),
    couponNameLen("op.push.couponNameLen"),
    priceLen("op.push.priceLen"),
    profitLen("op.push.profitLen"),
    pushVendor("op.push.pushVendor"),

    openNightSleep("op.push.openNightSleep"),
    nightStart("op.push.nightStart"),
    nightEnd("op.push.nightEnd"),
    openCallback("op.push.OpenCallback"),

    clAccount("op.push.clAccount"),
    clPassword("op.push.clPassword"),
    clRequestServerUrl("op.push.clRequestServerUrl"),
    clPullMoUrl("op.push.clPullMoUrl"),
    clReportRequestUrl("op.push.clReportRequestUrl"),


    montnetsAccount("op.push.montnetsAccount"),
    montnetsPassword("op.push.montnetsPassword"),
    montnetsMasterIpAddress("op.push.montnetsMasterIpAddress"),

    dailyPollingMins("op.push.dailyPollingMins"),
    batchPollingMins("op.push.batchPollingMins"),
    moPollingMins("op.push.moPollingMins"),
    rptPollingMins("op.push.rptPollingMins"),

    signature("op.push.signature"),
    signatureFlag("op.push.signature_flag"),
    unsubscribe("op.push.unsubscribe"),
    unsubscribeFlag("op.push.unsubscribe_flag");


    private String keyCode;


    PushPropertiesEnum(String keyCode) {
        this.keyCode = keyCode;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }
}

