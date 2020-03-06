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
     * 推送方式
     */
    private String pushType = "SMS";

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
}
