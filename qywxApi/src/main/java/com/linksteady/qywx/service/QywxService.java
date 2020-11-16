package com.linksteady.qywx.service;

import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.storage.impl.RedisConfigStorageImpl;

public interface QywxService {

    RedisConfigStorageImpl getRedisConfigStorage();

    /**
     * <pre>
     * 验证推送过来的消息的正确性
     * 详情请见: https://work.weixin.qq.com/api/doc#90000/90139/90968/消息体签名校验
     * </pre>
     *
     * @param msgSignature 消息签名
     * @param timestamp    时间戳
     * @param nonce        随机数
     * @param data         微信传输过来的数据，有可能是echoStr，有可能是xml消息
     */
    boolean checkSignature(String msgSignature, String timestamp, String nonce, String data);


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



}
