package com.linksteady.operate.domain;

import lombok.Data;

/**
 * 每日运营--个体效果结果类
 *
 * @author hxcao
 * @date 2019-10-28
 */
@Data
public class DailyPersonal {
    // 用户ID
    private String userId;

    // 是否转化
    private String isConvert;

    // 推送时段
    private String pushPeriod;

    // 转化时段
    private String convertPeriod;

    // 转化间隔(天)
    private String convertInterval;

    // 推送spu
    private String pushSpu;

    // 转化spu
    private String convertSpu;

    // 推送spu是否转化
    private String spuIsConvert;

    // 用户价值
    private String userValue;

    // 用户活跃度
    private String pathActive;

    // 用户名
    private String userName;

    // 手机号
    private String mobile;

    // 推送日期
    private String pushDate;

    // 转化日期
    private String convertDate;
}
