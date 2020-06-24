package com.linksteady.operate.vo;

import com.linksteady.operate.config.PushConfig;
import lombok.Synchronized;

/**
 * 每日运营渠道信息配置的VO
 */
public class SourceConfigVO {

    private static SourceConfigVO instance=new SourceConfigVO();

    private SourceConfigVO()
    {

    }

    public static SourceConfigVO getInstance(PushConfig pushConfig)
    {
        //重新加载属性
        updateProperties(pushConfig);
        return instance;
    }

    @Synchronized
    private static void updateProperties(PushConfig pushConfig)
    {
        instance.setSourceName(pushConfig.getSoureName())
                .setCouponSendType(pushConfig.getCouponSendType())
                //如果自行领券，则优惠券链接可用  自动打券，优惠券链接不可用
                .setCouponUrlEnabled((null==pushConfig.getCouponSendType()&&"A".equals(pushConfig.getCouponSendType()))?"Y":"N")
                .setCouponName(pushConfig.getCouponName())
                .setProdName(pushConfig.getProdName())
                .setUrl(pushConfig.getUrl())
                .setProdUrlEnabled(pushConfig.getProdUrlEnabled())
                .setPrice(pushConfig.getPrice())
                .setProfit(pushConfig.getProfit())
                .setShortUrlLen(pushConfig.getShortUrlLen())
                .setProdNameLen(pushConfig.getProdNameLen())
                .setPriceLen(pushConfig.getPriceLen())
                .setCouponNameLen(pushConfig.getCouponNameLen())
                .setProfitLen(pushConfig.getProfitLen())
                .setSmsLengthLimit(pushConfig.getSmsLengthLimit())
                .setSignature(pushConfig.getSignature())
                .setUnsubscribe(pushConfig.getUnsubscribe())
                .setSignatureFlag("Y".equalsIgnoreCase(pushConfig.getSignatureFlag())?"包含":"不包含")
                .setUnsubscribeFlag("Y".equalsIgnoreCase(pushConfig.getUnsubscribeFlag())?"包含":"不包含")
        ;
    }

    /**
     * 渠道名称
     */
    private String sourceName;

    /**
     * 优惠券发放方式
     */
    private String couponSendType;

    /**
     * 优惠券链接是否可用
     */
    private String couponUrlEnabled;

    /**
     * 商品详情页链接是否可用
     */
    private String prodUrlEnabled;

    /**
     * 短链样例
     */
    private String url;

    /**
     * 商品名称样例
     */
    private String prodName;

    /**
     * 补贴名称样例
     */
    private String couponName;

    /**
     * 商品最低价格样例
     */
    private String price;

    /**
     * 利益点样例
     */
    private String profit;

    /**
     * 短链长度
     */
    private Integer shortUrlLen;

    /**
     * 优惠券名称长度
     */
    private Integer couponNameLen;

    /**
     * 商品名称长度
     */
    private Integer prodNameLen;

    /**
     * 文案总长度
     */
    private Integer smsLengthLimit;

    /**
     * 价格长度
     */
    private Integer priceLen;

    /**
     * 利益点长度
     */
    private Integer profitLen;

    /**
     * 是否包含签名信息
     * @param sourceName
     * @return
     */
    private String signatureFlag;

    /**
     * 是否包含退订信息
     * @param sourceName
     * @return
     */
    private String unsubscribeFlag;

    /**
     * 签名信息
     * @param sourceName
     * @return
     */
    private String signature;

    /**
     * 退订信息
     * @param sourceName
     * @return
     */
    private String unsubscribe;


    private SourceConfigVO setSourceName(String sourceName) {
        this.sourceName = sourceName;
        return this;
    }

    private SourceConfigVO setCouponSendType(String couponSendType) {
        this.couponSendType = couponSendType;
        return this;
    }

    private SourceConfigVO setCouponUrlEnabled(String couponUrlEnabled) {
        this.couponUrlEnabled = couponUrlEnabled;
        return this;
    }

    private SourceConfigVO setProdUrlEnabled(String prodUrlEnabled) {
        this.prodUrlEnabled = prodUrlEnabled;
        return this;
    }

    private SourceConfigVO setUrl(String url) {
        this.url = url;
        return this;
    }

    private SourceConfigVO setProdName(String prodName) {
        this.prodName = prodName;
        return this;
    }

    private SourceConfigVO setCouponName(String couponName) {
        this.couponName = couponName;
        return this;
    }

    private SourceConfigVO setPrice(String price) {
        this.price = price;
        return this;
    }

    private SourceConfigVO setProfit(String profit) {
        this.profit = profit;
        return this;
    }

    private SourceConfigVO setShortUrlLen(Integer shortUrlLen) {
        this.shortUrlLen = shortUrlLen;
        return this;
    }

    private SourceConfigVO setCouponNameLen(Integer couponNameLen) {
        this.couponNameLen = couponNameLen;
        return this;
    }

    private SourceConfigVO setProdNameLen(Integer prodNameLen) {
        this.prodNameLen = prodNameLen;
        return this;
    }

    private SourceConfigVO setSmsLengthLimit(Integer smsLengthLimit) {
        this.smsLengthLimit = smsLengthLimit;
        return this;
    }

    private SourceConfigVO setPriceLen(Integer priceLen) {
        this.priceLen = priceLen;
        return this;
    }

    private SourceConfigVO setProfitLen(Integer profitLen) {
        this.profitLen = profitLen;
        return this;
    }

    private SourceConfigVO setSignatureFlag(String signatureFlag) {
        this.signatureFlag = signatureFlag;
        return this;
    }

    private SourceConfigVO setUnsubscribeFlag(String unsubscribeFlag) {
        this.unsubscribeFlag = unsubscribeFlag;
        return this;
    }

    private SourceConfigVO setSignature(String signature) {
        this.signature = signature;
        return this;
    }

    private SourceConfigVO setUnsubscribe(String unsubscribe) {
        this.unsubscribe = unsubscribe;
        return this;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getCouponSendType() {
        return couponSendType;
    }

    public String getCouponUrlEnabled() {
        return couponUrlEnabled;
    }

    public String getProdUrlEnabled() {
        return prodUrlEnabled;
    }

    public String getUrl() {
        return url;
    }

    public String getProdName() {
        return prodName;
    }

    public String getCouponName() {
        return couponName;
    }

    public String getPrice() {
        return price;
    }

    public String getProfit() {
        return profit;
    }

    public Integer getShortUrlLen() {
        return shortUrlLen;
    }

    public Integer getCouponNameLen() {
        return couponNameLen;
    }

    public Integer getProdNameLen() {
        return prodNameLen;
    }

    public Integer getSmsLengthLimit() {
        return smsLengthLimit;
    }

    public Integer getPriceLen() {
        return priceLen;
    }

    public Integer getProfitLen() {
        return profitLen;
    }

    public String getSignatureFlag() {
        return signatureFlag;
    }

    public String getUnsubscribeFlag() {
        return unsubscribeFlag;
    }

    public String getSignature() {
        return signature;
    }

    public String getUnsubscribe() {
        return unsubscribe;
    }
}
