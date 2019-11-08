package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.util.OkHttpUtil;
import com.linksteady.operate.util.UrlUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.stream.IntStream;

/**
 * 根据长链接生成短链接的服务
 */
@Service
@Slf4j
public class ShortUrlServiceImpl implements ShortUrlService {

    @Autowired
    DailyProperties dailyProperties;

    private static final String TOKEN="181cb047026eef2735bdec65a81293f7";
    final static String CREATE_API = "https://dwz.cn/admin/v2/create";

    private static final String jumpUrl="https://h5.m.taobao.com/ecrm/jump-to-app.html?target_url=";

    /**
     * 根据传入的商品长链接生成短链接 (会考虑是否包裹一层唤醒淘宝APP的转跳)
     * @param longUrl 长网址：即原网址
     * @return  成功：短网址 失败：返回空字符串
     */
    @Override
    @SneakyThrows
    public String genProdShortUrl(String longUrl) {

        //判断是否是测试环节生成模拟短链接
        if("Y".equals(dailyProperties.getIsTestEnv()))
        {
            return dailyProperties.getDemoShortUrl();
        }else
        {
            String newLongUrl="";
            //判断是否需要进行转跳 唤醒淘宝APP
            if("Y".equals(dailyProperties.getIsAliApp()))
            {
               //进行一层转跳
                newLongUrl=jumpUrl+ UrlUtil.getURLEncoderString(longUrl);
            }else
            {
                newLongUrl=longUrl;
            }

            //生成短链
            return produceShortUrl(newLongUrl);

        }
    }

    /**
     * 根据商品的长链接直接生成短链接(直接生成)
     * @param longUrl
     * @return
     */
    @Override
    public String genProdShortUrlDirect(String longUrl) {
        if("Y".equals(dailyProperties.getIsTestEnv()))
        {
            return dailyProperties.getDemoShortUrl();
        }else
        {
            return produceShortUrl(longUrl);
        }
    }

    /**
     * 根据商品ID 生成商品明细页的短链接((会考虑是否包裹一层唤醒淘宝APP的转跳))
     * @param productId
     * @return
     */
    @Override
    public String genProdShortUrlByProdId(String productId) {
        //判断是否是测试环节生成模拟短链接
        if("Y".equals(dailyProperties.getIsTestEnv()))
        {
            return dailyProperties.getDemoShortUrl();
        }else
        {
            String newLongUrl=dailyProperties.getProductUrl().replace("$PRODUCT_ID",productId);
            //判断是否需要进行转跳 唤醒淘宝APP
            if("Y".equals(dailyProperties.getIsAliApp()))
            {
                //进行一层转跳
                newLongUrl=jumpUrl+ UrlUtil.getURLEncoderString(newLongUrl);
            }

            //生成短链
            return produceShortUrl(newLongUrl);
        }
    }

    /**
     * 根据优惠券的链接生成长链接(会考虑是否包裹一层唤醒淘宝APP的转跳)
     */
    @Override
    public String genConponShortUrl(String couponUrl) {
        //判断是否是测试环节生成模拟短链接
        if("Y".equals(dailyProperties.getIsTestEnv()))
        {
            return dailyProperties.getDemoShortUrl();
        }else
        {
            //判断是否需要进行转跳 唤醒淘宝APP
            if("Y".equals(dailyProperties.getIsAliApp()))
            {
                //进行一层转跳
                return produceShortUrl(jumpUrl+ UrlUtil.getURLEncoderString(couponUrl));
            }else
            {
                return produceShortUrl(couponUrl);
            }
        }
    }

    /**
     * 根据优惠券的链接生成长链接（直接生成）
     */
    @Override
    public String genConponShortUrlDirect(String couponUrl) {
        if("Y".equals(dailyProperties.getIsTestEnv()))
        {
            return dailyProperties.getDemoShortUrl();
        }else
        {
            return produceShortUrl(couponUrl);
        }
    }

    /**
     * 根据传入的长链接 生成短链接
     * @param longUrl
     * @return
     */
    private String produceShortUrl(String longUrl)
    {
        //todo 此处应该进行一次查找 如果找不到，再调用API进行生成

        OkHttpClient client=OkHttpUtil.getOkHttpClient();
        //termOfValidity 有效期：默认值为long-term  "1-year"：1年
        String termOfValidity="1-year";

        String params = "{\"Url\":\""+ longUrl + "\",\"TermOfValidity\":\""+ termOfValidity + "\"}";

        String shortUrl= null;
        String errMsg="";
        Integer errCode=null;
        try {
            Request request = new Request.Builder()
                    .url(CREATE_API)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Token",TOKEN)
                    .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), params))
                    .build();

            ResponseBody responseBody=client.newCall(request).execute().body();

            JSONObject jsonObject = JSON.parseObject(responseBody.string());
            shortUrl = jsonObject.getString("ShortUrl");
            errCode=jsonObject.getInteger("Code");
            errMsg=jsonObject.getString("ErrMsg");

            if(null==errCode||errCode!=0)
            {
                shortUrl="";
                //todo 错误上报

                log.error("长链 {} 生成短链错误,错误原因为{},错误代码为{}",longUrl,errMsg,errCode);
            }
        } catch (Exception e) {
            shortUrl="";
            //todo 错误上报

            log.error("长链 {} 生成短链错误",longUrl,e);
        }
        return shortUrl;
    }

}
