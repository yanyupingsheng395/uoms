package com.linksteady.operate.domain;

import lombok.Data;

/**
 * 每日运营配置信息
 *
 * @author
 */
@Data
public class DailyProperties {

    /**
     * 避免重复推送的天数
     */
    private int repeatPushDays = 7;

    /**
     * 是否开启推送
     */
    private String pushFlag = "Y";

    /**
     * 效果统计的天数
     */
    private int statsDays = 20;

    /**
     * 推送方式  (不考虑页面维护)
     */
    private String pushType = "SMS";

    /**
     * 开启预警 (不考虑页面维护)
     */
    private String openAlert = "Y";

    /**
     * 预警手机号码 (不考虑页面维护)
     */
    private String alertPhone;

    /**
     * 推送方式 IMME:IMME立即推送 AI:AI智能推送
     */
    private String pushMethod;

    /**
     * 短信内容的长度限制
     */
    private int smsLengthLimit = 66;

    /**
     * 商品详情页的组装格式 (不考虑页面维护)
     */
    private String productUrl = "https://detail.tmall.com/item.htm?id=$PRODUCT_ID";

    /**
     * 包装短链是否需要包装成可唤醒淘宝APP (不考虑页面维护) Y表示是 N表示否
     */
   // private String isAliApp = "Y";

    /**
     * 短链是否返回模拟链接 （测试环境适用）
     */
    private String isTestEnv = "Y";

    /**
     * 模拟短链接的样例链接 (天猫首页)
     */
    private String demoShortUrl = "https://tb.cn.hn/t8n";

    /**
     * 短链的长度
     */
    private int shortUrlLen;

    /**
     * 产品名称的最大长度
     */
    private int prodNameLen;

    /**
     * 当前修改用户
     */
    private String currentUser;

    /**
     * 优惠券发送方式 A自行领取 B系统发送
     */
    private String couponSendType;

    /**
     * 优惠券名称的长度
     */
    private int couponNameLen;
}
