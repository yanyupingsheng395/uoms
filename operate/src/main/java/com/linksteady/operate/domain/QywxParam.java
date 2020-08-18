package com.linksteady.operate.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class QywxParam {

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
     * 上次修改人
     */
    private String lastUpdateBy;

    /**
     * 上次修改时间
     */
    private Date lastUpdateDt;

    /**
     * 版本号
     */
    private int version;

    /**
     * 最后一次同步订单的时间戳
     */
    private LocalDateTime lastSyncOrderDt;

    /**
     * 最后一次同步订单的linux时间戳
     */
    private long lastSyncOrderTimes;

}
