package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.domain.QywxMessage;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.operate.service.QywxMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * 企业微信消息服务类
 */
@Slf4j
@Service
public class QywxMessageServiceImpl implements QywxMessageService {

    @Autowired
    ConfigService configService;

    /**
     * 给企业微信发送单人消息
     * @param message
     * @param sender
     * @param externalUserList
     */
    @Override
    public String pushQywxMessage(QywxMessage message, String sender, List<String> externalUserList) {
        String corpId=configService.getValueByName(ConfigEnum.qywxCorpId.getKeyCode());
        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
        String signature= SHA1.gen(timestamp);
        String qywcDomainUrl=configService.getValueByName(ConfigEnum.qywxDomainUrl.getKeyCode());
        String url=qywcDomainUrl+"/push/addMsgTemplate?corpId="+corpId+"&timestamp="+timestamp+"&signature="+signature;

        //构造推送参数
        JSONObject param=new JSONObject();
        param.put("chat_type","single");
        param.put("external_userid",JSONArray.parseArray(JSON.toJSONString(externalUserList)));
        param.put("sender",sender);

        JSONObject tempContent=null;
        //文本
        if(StringUtils.isNotEmpty(message.getText()))
        {
            tempContent=new JSONObject();
            tempContent.put("content",message.getText());
            param.put("text",tempContent);
        }

        //图片
        if(StringUtils.isNotEmpty(message.getImgMediaId())||StringUtils.isNotEmpty(message.getImgPicUrl()))
        {
            tempContent=new JSONObject();
            if(StringUtils.isNotEmpty(message.getImgMediaId()))
            {
                tempContent.put("media_id",message.getImgMediaId());
            }
            if(StringUtils.isNotEmpty(message.getImgPicUrl()))
            {
                tempContent.put("pic_url",message.getImgPicUrl());
            }
            param.put("image",tempContent);
        }
        //链接
        if(StringUtils.isNotEmpty(message.getLinkTitle()))
        {
            tempContent=new JSONObject();
            tempContent.put("title",message.getLinkTitle());

            if(StringUtils.isNotEmpty(message.getLinkPicUrl()))
            {
                tempContent.put("picurl",message.getLinkPicUrl());
            }

            if(StringUtils.isNotEmpty(message.getLinkDesc()))
            {
                tempContent.put("desc",message.getLinkDesc());
            }

            if(StringUtils.isNotEmpty(message.getLinkUrl()))
            {
                tempContent.put("url",message.getLinkUrl());
            }

            param.put("link",tempContent);
        }

        //小程序
        if(StringUtils.isNotEmpty(message.getMpTitle()))
        {
            tempContent=new JSONObject();
            tempContent.put("title",message.getMpTitle());
            tempContent.put("pic_media_id",message.getMpPicMediaId());
            tempContent.put("appid",message.getMpAppid());
            tempContent.put("page",message.getMpPage());

            param.put("miniprogram",tempContent);
        }

        String result= OkHttpUtil.postRequestByJson(url,param.toJSONString());
        return result;
    }
}
