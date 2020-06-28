package com.linksteady.operate.domain.enums;

/**
 * 推送属性类的属性名及其在T_CONFIG中配置CODE的映射枚举类
 */
public enum  PushPropertiesEnum {
    repeatPushDays("op.push.repeatPushDays"),

    pushFlag("op.push.pushFlag"),
    pushMethod("op.push.pushMethod"),
    isTestEnv("op.push.isTestEnv"),

    couponSendType("op.source.couponSendType"),

    smsLengthLimit("op.push.smsLengthLimit"),
    shortUrlLen("op.push.shortUrlLen"),
    prodNameLen("op.push.prodNameLen"),
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
    unsubscribeFlag("op.push.unsubscribe_flag"),
    sendSignatureFlag("op.push.send_signature_flag"),
    sendUnsubscribeFlag("op.push.send_unsubscribe_flag"),

    url("op.sms.url"),
    prodName("op.sms.prodname"),
    couponName("op.sms.couponname"),
    price("op.sms.price"),
    profit("op.sms.profit"),

    sourceName("op.source.name"),
    smsEnabled("op.source.smsEnabled"),
    qywxEnabled("op.source.qywxEnabled"),
    wxofficialEnabled("op.source.wxofficialEnabled"),
    prodUrlEnabled("op.source.produrl_enabled");

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

