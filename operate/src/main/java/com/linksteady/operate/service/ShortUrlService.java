package com.linksteady.operate.service;

/**
 * 生成短链接的服务
 */
public interface ShortUrlService {

    /**
     * 根据商品的长链接直接生成短链接(会考虑是否包裹一层唤醒淘宝APP的转跳)
     * @param longUrl
     * @return
     */
    String genProdShortUrl(String longUrl,String sourceType);

    /**
     * 根据商品的长链接直接生成短链接(直接生成)
     * @param longUrl
     * @return
     */
    String genProdShortUrlDirect(String longUrl,String sourceType);


    /**
     * 根据商品ID 生成商品明细页的短链接((会考虑是否包裹一层唤醒淘宝APP的转跳))
     * @param productId
     * @return
     */
    String genProdShortUrlByProdId(String productId,String sourceType);

    /**
     * 根据优惠券的链接生成长链接(会考虑是否包裹一层唤醒淘宝APP的转跳)
     */
    String genConponShortUrl(String couponUrl,String sourceType);

    /**
     * 根据优惠券的链接生成长链接（直接生成）
     */
    String genConponShortUrlDirect(String couponUrl,String sourceType);

}
