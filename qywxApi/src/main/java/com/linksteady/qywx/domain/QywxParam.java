package com.linksteady.qywx.domain;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class QywxParam implements Serializable {

    /**
     * 企业微信每日加人上限
     */
    private int dailyAddNum;

    /**
     * 企业微信加人转化率
     */
    private double dailyAddRate;

    private String dailyAddRateStr;

    /**
     * 最后一次修改人
     */
    private String lastUpdateBy;
    /**
     * 最后一次修改时间
     */
    private String lastUpdateDt;
    /**
     * 企业微信每日推送总人数
     */
    private int dailyApplyNum;

    /**
     * 自动触发人数(根据订单自动触发)
     */
    private int triggerNum;

    /**
     * 主动触发人数（手工推送）
     */
    private int activeNum;
    /**
     * 版本号
     */
    private int version;
    /**
     * 最后一次同步订单的时间戳
     */
    private LocalDateTime lastSyncOrderDt;

    /**
     * 小程序封面内容
     */
    private byte[] mediaContent;

    /**
     * 优惠券封面默认图片
     */
    private byte[] couponMediaContent;

    private String corpId;

    private String secret;

    private String ecEventToken;

    private String ecEventAesKey;

    private String mpAppId;


    private String enableWelcome;

}
