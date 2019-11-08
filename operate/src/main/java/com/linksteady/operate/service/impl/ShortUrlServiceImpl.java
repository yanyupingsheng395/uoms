package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.util.OkHttpUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
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

    private static final String TOKEN="181cb047026eef2735bdec65a81293f7";
    final static String CREATE_API = "https://dwz.cn/admin/v2/create";

    /**
     * 使用baidu提供的服务创建短网址 (会考虑是否包裹一层唤醒淘宝APP的转跳)
     * @param longUrl 长网址：即原网址
     * @return  成功：短网址 失败：返回空字符串
     */
    @Override
    @SneakyThrows
    public String genProdShortUrl(String longUrl) {

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
                log.error("长链 {} 生成短链错误,错误原因为{},错误代码为{}",longUrl,errMsg,errCode);
            }
        } catch (Exception e) {
            shortUrl="";
            log.error("长链 {} 生成短链错误",longUrl,e);
        }
        return shortUrl;
    }

    @Override
    public String genProdShortUrlDirect(String longUrl) {
        return null;
    }

    @Override
    public String genProdShortUrlByProdId(String productId) {
        return null;
    }

    @Override
    public String genConponShortUrl(String couponUrl) {
        return null;
    }

    @Override
    public String genConponShortUrlDirect(String couponUrl) {
        return null;
    }

}
