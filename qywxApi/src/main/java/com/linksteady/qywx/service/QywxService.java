package com.linksteady.qywx.service;

import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.storage.impl.RedisConfigStorageImpl;


public interface QywxService {

    RedisConfigStorageImpl getRedisConfigStorage();

    /**
     * 获取 access_token
     */
    String getAccessToken() throws WxErrorException;

    /**
     * 更新corpId和应用secret
     */
    void updateCorpInfo(String corpId,String secret);

    /**
     * 获取外部联系人事件的token
     */
    String getEcEventToken();

    /**
     * 获取外部联系人事件的aesKey
     */
    String getEcEventAesKey();

    /**
     * 更新外部联系人信息
     */
    void updateContact(String eventToken, String eventAesKey);

    /**
     * 是否开启了欢迎语 Y表示是 N表示否
     * @return
     */
    String getEnableWelcome();


    /**
     * 设置欢迎语的状态
     * @param status
     */
    void setEnableWelcome(String status);

    /**
     * 获取小程序appID
     */
    String getMpAppId();


    /**
     * 设置小程序appid
     */
    void setMpAppId(String mpAppId);

}
