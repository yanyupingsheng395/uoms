package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.domain.Tconfig;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.operate.dao.ShortUrlMapper;
import com.linksteady.operate.domain.PushProperties;
import com.linksteady.operate.domain.ShortUrlInfo;
import com.linksteady.operate.service.ShortUrlService;
import com.linksteady.operate.util.OkHttpUtil;
import com.linksteady.operate.util.UrlUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * 根据长链接生成短链接的服务
 */
@Service
@Slf4j
public class ShortUrlServiceImpl implements ShortUrlService {

    @Autowired
    private PushProperties pushProperties;

    @Autowired
    ShortUrlMapper shortUrlMapper;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Autowired
    private RedisTemplate redisTemplate;

    private String redisDataKey = "SHORT_URL_KEY";

    private static final String TOKEN = "181cb047026eef2735bdec65a81293f7";
    final static String CREATE_API = "https://dwz.cn/admin/v2/create";

    private static final String jumpUrl = "https://h5.m.taobao.com/ecrm/jump-to-app.html?target_url=";


    private void setShortUrlToRedis() {
        log.info("开始将短链的数据同步到redis");
        HashOperations hashOperations = redisTemplate.opsForHash();
        List<ShortUrlInfo> shortUrlInfos = shortUrlMapper.getDataList();
        shortUrlInfos.stream().forEach(x -> {
            hashOperations.putIfAbsent(redisDataKey, x.getLongUrl(), x.getShortUrl());
        });
        //已最早失效的那个url的失效时间作为整个redisDataKey的失效时间
        ShortUrlInfo shortUrlInfo = shortUrlInfos.stream().min(Comparator.comparing(ShortUrlInfo::getValidateDate)).get();
        redisTemplate.expireAt(redisDataKey, shortUrlInfo.getValidateDate());
        log.info("结束同步短链数据到redis");
    }


    /**
     * 根据传入的商品长链接生成短链接 (会考虑是否包裹一层唤醒淘宝APP的转跳)
     *
     * @param longUrl 长网址：即原网址
     * @return 成功：短网址 失败：返回空字符串
     */
    @Override
    @SneakyThrows
    public String genProdShortUrl(String longUrl, String sourceType) {

        //判断是否是测试环节生成模拟短链接
        if ("Y".equals(pushProperties.getIsTestEnv())) {
            return pushProperties.getDemoShortUrl();
        } else {
            String newLongUrl = "";
            //判断是否需要进行转跳 唤醒淘宝APP  如果长链接中包含 taobao.com tmall.com 则进行包裹
            if (longUrl.indexOf("taobao.com") != -1 || longUrl.indexOf("tmall.com") != -1) {
                //进行一层转跳
                newLongUrl = jumpUrl + UrlUtil.getURLEncoderString(longUrl);
            } else {
                newLongUrl = longUrl;
            }

            //生成短链
            return produceShortUrl(newLongUrl, sourceType);

        }
    }

    /**
     * 根据商品的长链接直接生成短链接(直接生成)
     *
     * @param longUrl
     * @return
     */
    @Override
    public String genProdShortUrlDirect(String longUrl, String sourceType) {
        if ("Y".equals(pushProperties.getIsTestEnv())) {
            return pushProperties.getDemoShortUrl();
        } else {
            return produceShortUrl(longUrl, sourceType);
        }
    }

    /**
     * 根据商品ID 生成商品明细页的短链接((会考虑是否包裹一层唤醒淘宝APP的转跳))
     *
     * @param productId
     * @return
     */
    @Override
    public String genProdShortUrlByProdId(String productId, String sourceType) {
        //判断是否是测试环节生成模拟短链接
        if ("Y".equals(pushProperties.getIsTestEnv())) {
            return pushProperties.getDemoShortUrl();
        } else {
            String newLongUrl = pushProperties.getProductUrl().replace("$PRODUCT_ID", productId);
            //判断是否需要进行转跳 唤醒淘宝APP
            if (newLongUrl.indexOf("taobao.com") != -1 || newLongUrl.indexOf("tmall.com") != -1) {
                //进行一层转跳
                newLongUrl = jumpUrl + UrlUtil.getURLEncoderString(newLongUrl);
            }

            //生成短链
            return produceShortUrl(newLongUrl, sourceType);
        }
    }

    /**
     * 根据优惠券的链接生成长链接(会考虑是否包裹一层唤醒淘宝APP的转跳)
     */
    @Override
    public String genConponShortUrl(String couponUrl, String sourceType) {
        //判断是否是测试环节生成模拟短链接
        if ("Y".equals(pushProperties.getIsTestEnv())) {
            return pushProperties.getDemoShortUrl();
        } else {
            //判断是否需要进行转跳 唤醒淘宝APP
            if (couponUrl.indexOf("taobao.com") != -1 || couponUrl.indexOf("tmall.com") != -1) {
                //进行一层转跳
                return produceShortUrl(jumpUrl + UrlUtil.getURLEncoderString(couponUrl), sourceType);
            } else {
                return produceShortUrl(couponUrl, sourceType);
            }
        }
    }

    /**
     * 根据长链接生成长链接（直接生成）
     */
    @Override
    public String genShortUrlDirect(String longUrl, String sourceType) {
        if ("Y".equals(pushProperties.getIsTestEnv())) {
            return pushProperties.getDemoShortUrl();
        } else {
            return produceShortUrl(longUrl, sourceType);
        }
    }

    /**
     * 根据传入的长链接 生成短链接
     *
     * @param longUrl
     * @return
     */
    private synchronized String produceShortUrl(String longUrl, String sourceType) {
        //进行一次查找 如果找不到，再调用API进行生成
        if(!redisTemplate.hasKey(redisDataKey)) {
            setShortUrlToRedis();
        }
        String shortUrl = (String) redisTemplate.opsForHash().get(redisDataKey, longUrl);
        if (StringUtils.isEmpty(shortUrl)) {
            shortUrl = shortUrlMapper.selectShortUrlByLongUrl(longUrl);
            if (StringUtils.isNotEmpty(shortUrl)) {
                redisTemplate.opsForHash().putIfAbsent(redisDataKey, longUrl, shortUrl);
                return shortUrl;
            }
        }
        if (StringUtils.isEmpty(shortUrl)) {
            OkHttpClient client = OkHttpUtil.getOkHttpClient();
            //termOfValidity 有效期：默认值为long-term  "1-year"：1年
            String termOfValidity = "1-year";

            String params = "{\"Url\":\"" + longUrl + "\",\"TermOfValidity\":\"" + termOfValidity + "\"}";
            String errMsg = "";
            Integer errCode = null;
            try {
                Request request = new Request.Builder()
                        .url(CREATE_API)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Token", TOKEN)
                        .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), params))
                        .build();

                log.info("调用api为长链接:{},生成短链", longUrl);
                ResponseBody responseBody = client.newCall(request).execute().body();

                JSONObject jsonObject = JSON.parseObject(responseBody.string());
                shortUrl = jsonObject.getString("ShortUrl");
                errCode = jsonObject.getInteger("Code");
                errMsg = jsonObject.getString("ErrMsg");

                if (null == errCode || errCode != 0) {
                    shortUrl = "error";
                    //进行异常日志的上报
                    exceptionNoticeHandler.exceptionNotice("生成短链错误");
                    log.error("长链 {} 生成短链错误,错误原因为{},错误代码为{}", longUrl, errMsg, errCode);
                }
            } catch (Exception e) {
                shortUrl = "error";
                //进行异常日志的上报
                exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e), 1, 512));
                log.error("长链 {} 生成短链错误", longUrl, e);
            }
            if (!"error".equals(shortUrl)) {
                //短链的失效时间
                LocalDate validateDate = LocalDate.now().plus(1, ChronoUnit.YEARS);
                ZoneId zone = ZoneId.systemDefault();
                Instant instant = validateDate.atStartOfDay().atZone(zone).toInstant();
                Date validateDate2 = Date.from(instant);

                //写入短链生成记录
                ShortUrlInfo shortUrlInfo = new ShortUrlInfo(longUrl, shortUrl, validateDate2, sourceType);
                //判断长链是否存在，不存在则insert 存在则更新update_dt
                if (shortUrlMapper.selectCountByLongUrl(longUrl) > 0) {
                    shortUrlMapper.updateShortUrlValidateDate(validateDate2);
                } else {
                    shortUrlMapper.InsertShortUrl(shortUrlInfo);
                }
                redisTemplate.opsForHash().putIfAbsent(redisDataKey, longUrl, shortUrl);
            }
            return shortUrl;
        }
        return shortUrl;
    }
}
