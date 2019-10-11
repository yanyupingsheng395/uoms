package com.linksteady.operate.service;

public interface ShortUrlService {

    String produceShortUrl(String appid,String userId,String longUrl);

    String produceShortUrlByBaidu(String longUrl);
}
