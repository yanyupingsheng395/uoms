package com.linksteady.operate.domain.enums;

/**
 * 发送给推送端的推送信号枚举类
 * @author huang
 * @date
 */
public enum  PushSignalEnum {
    SIGNAL_START("start","启动推送服务"),
    SIGNAL_STOP("stop","关闭推送服务"),
    SIGNAL_REFRESH("refresh","刷新配置"),
    SIGNAL_PRINT("print","打印配置");

    private String signalCode;

    private String signalDesc;

    PushSignalEnum(String signalCode, String signalDesc) {
        this.signalCode = signalCode;
        this.signalDesc = signalDesc;
    }

    public String getSignalCode() {
        return signalCode;
    }

    public void setSignalCode(String signalCode) {
        this.signalCode = signalCode;
    }

    public String getSignalDesc() {
        return signalDesc;
    }

    public void setSignalDesc(String signalDesc) {
        this.signalDesc = signalDesc;
    }
}
