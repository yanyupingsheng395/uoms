package com.linksteady.qywx.domain;


import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

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
     * 版本号
     */
    private int version;
    /**
     * 最后一次同步订单的时间戳
     */
    private LocalDateTime lastSyncOrderDt;
    /**
     * 组织结构是有更新
     */
    private String partyChangeFlag;
    /**
     * 授权范围是否有更新
     */
    private String authScopeChangeFlag;
    /**
     * 通讯录是否有更新
     */
    private String followUserChangeFlag;
    /**
     * 小程序封面media_id
     */
    private String mediaId;
    /**
     * 小程序封面失效时间
     */
    private LocalDateTime mediaExpireDate;
    /**
     * 小程序封面内容
     */
    private byte[] mediaContent;
    /**
     * 欢迎语小程序封面mediaId
     */
    private String wcMdiaId;
    /**
     * 欢迎语小程序封面失效时间
     */
    private LocalDateTime wcMediaExpireDate;
    /**
     * 欢迎语小程序封面内容
     */
    private byte[] wxMediaContent;
    /**
     * 优惠券封面默认图片
     */
    private byte[] couponMediaContent;
}
