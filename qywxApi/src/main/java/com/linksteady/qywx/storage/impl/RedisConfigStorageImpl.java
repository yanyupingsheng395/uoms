package com.linksteady.qywx.storage.impl;

import com.linksteady.qywx.storage.RedisConfigStorage;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * redis存储各种配置参数的配置类
 */
@Slf4j
public class RedisConfigStorageImpl implements RedisConfigStorage {

    private volatile String baseApiUrl;

    protected final static String  QYWX_APP_SECRET="qywx:app:secret";
    protected final static String  QYWX_APP_ACCESS_TOKEN="qywx:app:accesstoken";
    protected final static String  QYWX_CORP_ID="qywx:corpid";

    protected final static String  QYWX_EC_EVNET_TOKEN="qywx:ec:token";
    protected final static String  QYWX_EC_EVNET_AESKEY="qywx:ec:aeskey";

    protected final static String  QYWX_ENABLED_WELCOME="qywx:ec:welcome";
    protected final static String  QYWX_MP_APP_ID="qywx:mpappid";

    private JedisPool jedisPool;

    public RedisConfigStorageImpl(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public String getApiUrl(String path) {
        if (baseApiUrl == null) {
            baseApiUrl = "https://qyapi.weixin.qq.com";
        }
        return baseApiUrl + path;
    }

    @Override
    public String getSecret() {
        try(Jedis jedis=jedisPool.getResource())
        {
            return jedis.get(QYWX_APP_SECRET);
        }
    }

    @Override
    public void setSecret(String secret) {
        try(Jedis jedis=jedisPool.getResource())
        {
            jedis.set(QYWX_APP_SECRET,secret);
        }
    }

    @Override
    public String getAccessToken() {
        try(Jedis jedis=jedisPool.getResource())
        {
            return jedis.get(QYWX_APP_ACCESS_TOKEN);
        }
    }

    @Override
    public void setAccessToken(String suitAccessToken, long expireIn) {
        try(Jedis jedis=jedisPool.getResource())
        {
            boolean keyExists=jedis.exists(QYWX_APP_ACCESS_TOKEN);
            if(keyExists)
            {
                jedis.del(QYWX_APP_ACCESS_TOKEN);
            }
            // NX是不存在时才set， XX是存在时才set， EX是秒，PX是毫秒
            jedis.set(QYWX_APP_ACCESS_TOKEN,suitAccessToken,"NX","EX",expireIn-200);
        }
    }

    @Override
    public void expireAccessToken() {
        try(Jedis jedis=jedisPool.getResource())
        {
            boolean keyExists=jedis.exists(QYWX_APP_ACCESS_TOKEN);
            if(keyExists)
            {
                jedis.del(QYWX_APP_ACCESS_TOKEN);
            }
        }
    }

    @Override
    public String getCorpId() {
        try(Jedis jedis=jedisPool.getResource())
        {
            return jedis.get(QYWX_CORP_ID);
        }
    }

    @Override
    public void setCorpId(String corpId) {
        try(Jedis jedis=jedisPool.getResource())
        {
            jedis.set(QYWX_CORP_ID,corpId);
        }
    }

    @Override
    public String getEcEventToken() {
        try(Jedis jedis=jedisPool.getResource())
        {
            return jedis.get(QYWX_EC_EVNET_TOKEN);
        }
    }

    @Override
    public void setEcEventToken(String ecEventToken) {
        try(Jedis jedis=jedisPool.getResource())
        {
            jedis.set(QYWX_EC_EVNET_TOKEN,ecEventToken);
        }
    }

    @Override
    public String getEcEventAesKey() {
        try(Jedis jedis=jedisPool.getResource())
        {
            return jedis.get(QYWX_EC_EVNET_AESKEY);
        }
    }

    @Override
    public void setEcEventAesKey(String ecEventAesKey) {
        try(Jedis jedis=jedisPool.getResource())
        {
            jedis.set(QYWX_EC_EVNET_AESKEY,ecEventAesKey);
        }
    }

    @Override
    public String getEnableWelcome() {
        try(Jedis jedis=jedisPool.getResource())
        {
            return jedis.get(QYWX_ENABLED_WELCOME);
        }
    }

    @Override
    public void setEnableWelcome(String enableWelcome) {
        try(Jedis jedis=jedisPool.getResource())
        {
            jedis.set(QYWX_ENABLED_WELCOME,enableWelcome);
        }
    }

    @Override
    public String getMpAppId() {
        try(Jedis jedis=jedisPool.getResource())
        {
            return jedis.get(QYWX_MP_APP_ID);
        }
    }

    @Override
    public void setMpAppId(String mpAppId) {
        try(Jedis jedis=jedisPool.getResource())
        {
            jedis.set(QYWX_MP_APP_ID,mpAppId);
        }
    }

}
