package com.linksteady.common.domain.enums;

/**
 * 推送属性类的属性名及其在T_CONFIG中配置CODE的映射枚举类
 */
public enum  ConfigEnum {
    repeatPushDays("op.push.repeatPushDays"),

    pushFlag("op.push.pushFlag"),
    pushMethod("op.push.pushMethod"),
    isTestEnv("op.push.isTestEnv"),

    couponSendType("op.source.couponSendType"),
    productDetailUrl("op.push.productDetailUrl"),

    shortUrlLen("op.push.shortUrlLen"),
    prodNameLen("op.push.prodNameLen"),
    couponNameLen("op.push.couponNameLen"),
    priceLen("op.push.priceLen"),
    profitLen("op.push.profitLen"),
    sourceNameLen("op.push.sourceNameLen"),

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
    prodUrlEnabled("op.source.produrl_enabled"),

    pathActiveList("op.daily.pathactive.list"),
    sendCouponUrl("op.sendCouponUrl"),
    sendCouponIdentityType("op.sendCouponIdentityType"),

    qywxSyncMsgResult("qywx.syncMsgResult");

    private String keyCode;

    ConfigEnum(String keyCode) {
        this.keyCode = keyCode;
    }

    public String getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(String keyCode) {
        this.keyCode = keyCode;
    }
}

