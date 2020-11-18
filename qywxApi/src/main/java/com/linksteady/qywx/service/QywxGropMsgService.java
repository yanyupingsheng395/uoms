package com.linksteady.qywx.service;


import com.alibaba.fastjson.JSONObject;

public interface QywxGropMsgService {
    /**
     * 获取企业群发消息发送结果
     */
     String getGroupMsgResult(JSONObject jsonObject) ;
    /**
     *获取微信corID
     */
     String getCorpId();
    /**
     *获取微信应用秘钥
     */
     String getSecret();

    /**
     *添加企业群发消息任务
     */
     String addMsgTemplate(JSONObject param);
}
