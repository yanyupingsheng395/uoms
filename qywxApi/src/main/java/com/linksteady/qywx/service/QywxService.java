package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.QywxParam;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.storage.impl.RedisConfigStorageImpl;

import java.util.Set;


public interface QywxService {

    RedisConfigStorageImpl getRedisConfigStorage();

    /**
     * 获取 access_token
     */
    String getAccessToken() throws WxErrorException;

    /**
     * 更新corpId和应用secret
     */
    void updateCorpInfo(String corpId,String secret,String agentId);

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


    /**
     * 读取corpId
     */
    String getCorpId();


    /**
     * 读取agentId
     */
    String getAgentId();

    /**
     * 获取企业的jsapi_ticket
     */
    String getJsapiTicket() throws WxErrorException;

    /**
     *获取应用的jsapiTicket
     */
    String getAgentJsapiTicket() throws WxErrorException;


    void saveFile(String title, String content);

    QywxParam getFileMessage();


    /**
     * 获取欢迎语导购白名单
     */
    Set<String> getWelcomeWhiteUserSet();

    /**
     * 设置欢迎语导购白名单
     */
    void setWelcomeWhiteUserName(String userList);
}
