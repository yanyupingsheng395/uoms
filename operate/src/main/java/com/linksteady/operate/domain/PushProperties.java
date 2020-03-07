package com.linksteady.operate.domain;

import lombok.Data;

/**
 * 推送配置信息
 *
 * @author
 */
@Data
public class PushProperties {

    /**
     * 避免重复推送的天数
     */
    private int repeatPushDays = 7;

    /**
     * 是否开启推送
     */
    private String pushFlag = "Y";

    /**
     * 推送方式 IMME:IMME立即推送 AI:AI智能推送
     */
    private String pushMethod;

    /**
     * 短信内容的长度限制
     */
    private int smsLengthLimit = 66;

    /**
     * 商品详情页的组装格式
     */
    private String productUrl = "https://detail.tmall.com/item.htm?id=$PRODUCT_ID";

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
     * 优惠券发送方式 A自行领取 B系统发送
     */
    private String couponSendType;

    /**
     * 优惠券名称的长度
     */
    private int couponNameLen;

    /**
     * 商品价格的最大长度
     */
    private int priceLen;

    /**
     * 短信供应商
     */
    private String pushVendor;

    /**
     * 休眠
     */
    private String openNightSleep;
    private int nightStart;
    private int nightEnd;


    /**
     * 是否开启状态报告和上行消息获取
     */
    private String OpenCallback;

    /**
     * 创蓝需要的参数
     */
    private String clAccount;
    private String clPassword;
    private String clRequestServerUrl;
    private String clPullMoUrl;
    private String clReportRequestUrl;

    /**
     * 梦网需要的参数
     */
    private String montnetsAccount;
    private String montnetsPassword;
    private String montnetsMasterIpAddress;
}
