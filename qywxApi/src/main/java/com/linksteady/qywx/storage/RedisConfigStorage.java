package com.linksteady.qywx.storage;

public interface RedisConfigStorage {

    /**
     * 读取企业微信 API Url.
     * 支持私有化企业微信服务器.
     */
    String getApiUrl(String path);

    /**
     * 获取应用的secret
     * @return
     */
    String getSecret();

    /**
     * 设置应用的secret
     * @param secret
     */
    void setSecret(String secret) ;


    /**
     * 获取 应用的accessToken
     */
    String getAccessToken();

    /**
     * 设置应用的accessToken
     * @param suitAccessToken
     * @param expireIn
     */
    void setAccessToken(String suitAccessToken,long expireIn);

    /**
     * 失效accessToken
     * @return
     */
    void expireAccessToken();

    String getCorpId();

    void setCorpId(String corpId);

    String getEcEventToken();

    void setEcEventToken(String ecEventToken);

    String getEcEventAesKey();

    void setEcEventAesKey(String ecEventAesKey);

}