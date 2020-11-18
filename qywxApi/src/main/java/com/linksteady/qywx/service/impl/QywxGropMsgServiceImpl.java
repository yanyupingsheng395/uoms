package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxGropMsgService;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.storage.impl.RedisConfigStorageImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QywxGropMsgServiceImpl implements QywxGropMsgService {

    @Autowired
    private QywxService qywxService;

    /**
     * 获取企业群发消息发送结果
     */
    @Override
    public String getGroupMsgResult(JSONObject param){
        String result="";
        try {
            String token=qywxService.getAccessToken();
            String url=WxPathConsts.DEFAULT_CP_BASE_URL+WxPathConsts.ExternalContacts.GET_GROUP_MSG_RESULT+token;
            result=OkHttpUtil.postRequestByJson(url,param.toJSONString());
        }catch (WxErrorException e){
            log.info("获取企业微信token失败{}",e);
        }
        return result;
    }


    /**
     *获取微信corID
     */
    @Override
    public String getCorpId() {
        RedisConfigStorageImpl storage = qywxService.getRedisConfigStorage();
        return storage.getCorpId();
    }

    /**
     *获取微信应用秘钥
     */
    @Override
    public String getSecret() {
        RedisConfigStorageImpl storage = qywxService.getRedisConfigStorage();
        return storage.getSecret();
    }

    @Override
    public String addMsgTemplate(JSONObject param) throws WxErrorException{
        String result="";
        String token=qywxService.getAccessToken();
        StringBuffer url=new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.ADD_MSG_TEMPLATE));
        url.append(token);
        result=OkHttpUtil.postRequestByJson(url.toString(),param.toJSONString());
        JSONObject object = JSONObject.parseObject(result);
        if(null==object||!"0".equals(object.getString("errcode"))){
            log.error("推送企业微信消息失败，接口返回结果为{}",result);
            return "";
        }
        return result;
    }
}
