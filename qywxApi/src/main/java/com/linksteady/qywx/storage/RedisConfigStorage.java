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


    String getEnableWelcome();

    void setEnableWelcome(String enableWelcome);

    /**
     * 获取小程序appID
     */
    String getMpAppId();

    /**
     * 设置小程序appid
     */
    void setMpAppId(String mpAppId);


    void setAgentId(String agentId);

    String getAgentId();

    String getJsapiTicket();

    void setJsapiTicket(String jsApiTicket,long expireIn);

    String getAgentJsapiTicket();

    void setAgentJsapiTicket(String agentJsApiTicket,long expireIn);

    String getWelcomeWhiteUserName();

    void setWelcomeWhiteUserName(String welcomeWhiteUserName);

}
