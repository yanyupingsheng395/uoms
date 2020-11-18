package com.linksteady.qywx.service;


import com.alibaba.fastjson.JSONObject;

public interface QywxGropMsgService {
    /**
     * 获取企业群发消息发送结果
     */
    public String getGroupMsgResult(JSONObject jsonObject) ;
    /**
     *获取微信corID
     */
    public String getCorpId();
    /**
     *获取微信应用秘钥
     */
    public String getSecret();

    /**
     *添加企业群发消息任务
     */
    public String addMsgTemplate(JSONObject param);
}
