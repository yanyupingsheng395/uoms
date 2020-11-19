package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.constant.CommonConstant;
import com.linksteady.common.domain.QywxMessage;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.QywxMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 企业微信消息服务类
 */
@Slf4j
@Service
public class QywxMessageServiceImpl implements QywxMessageService {
    private static final int maxRetryTimes=3;
    private static final String addMsg="/addMsgTemplate";

    @Autowired
    ConfigService configService;
    @Autowired
    private CommonFunService commonFunService;

    /**
     * 给企业微信发送单人消息
     * @param message
     * @param sender
     * @param externalUserList
     */
    @Override
    public String pushQywxMessage(QywxMessage message, String sender, List<String> externalUserList)throws Exception {
        int retryTimes=0;
        do{
            try{
                //构造推送参数
                JSONObject param=new JSONObject();
                param.put("chat_type","single");
                param.put("external_userid",JSONArray.parseArray(JSON.toJSONString(externalUserList)));
                param.put("sender",sender);

                JSONObject tempContent=null;
                //文本
                if(StringUtils.isNotEmpty(message.getText())){
                    tempContent=new JSONObject();
                    tempContent.put("content",message.getText());
                    param.put("text",tempContent);
                }

                //图片
                if(StringUtils.isNotEmpty(message.getImgMediaId())||StringUtils.isNotEmpty(message.getImgPicUrl())){
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
                if(StringUtils.isNotEmpty(message.getLinkTitle())) {
                    tempContent=new JSONObject();
                    tempContent.put("title",message.getLinkTitle());

                    if(StringUtils.isNotEmpty(message.getLinkPicUrl())){
                        tempContent.put("picurl",message.getLinkPicUrl());
                    }

                    if(StringUtils.isNotEmpty(message.getLinkDesc())){
                        tempContent.put("desc",message.getLinkDesc());
                    }

                    if(StringUtils.isNotEmpty(message.getLinkUrl())){
                        tempContent.put("url",message.getLinkUrl());
                    }

                    param.put("link",tempContent);
                }

                //小程序
                if(StringUtils.isNotEmpty(message.getMpTitle())){
                    tempContent=new JSONObject();
                    tempContent.put("title",message.getMpTitle());
                    tempContent.put("pic_media_id",message.getMpPicMediaId());
                    tempContent.put("appid",message.getMpAppid());
                    tempContent.put("page",message.getMpPage());

                    param.put("miniprogram",tempContent);
                }
                String addparam = JSONObject.toJSONString(param);
                SysInfoBo sysInfoBo = commonFunService.getSysInfoByCode(CommonConstant.QYWX_CODE);
                if(null==sysInfoBo||StringUtils.isEmpty(sysInfoBo.getSysDomain())){
                    throw new LinkSteadyException("企业微信应用未配置！");
                }
                StringBuffer url=new StringBuffer(sysInfoBo.getSysDomain());
                url.append(addMsg);
                String result= OkHttpUtil.postRequest(url.toString(),addparam);
                if(StringUtils.isEmpty(result))
                {
                    throw new LinkSteadyException("添加消息任务推送错误！");
                }
                return result;
            }catch (Exception e){
                log.error("添加消息任务处理异常，异常原因为{}",e);
                if(retryTimes+1>maxRetryTimes)
                {
                    log.warn("推送消息重试达到最大次数，接收到的参数为{}",message,sender,externalUserList.toString());
                }else{
                    //线程休眠
                    long delayMins=1000* (1 << retryTimes);
                    log.debug("添加消息任务处理错误，{}毫秒后进行{}次重试",delayMins,retryTimes);
                    try {
                        Thread.sleep(delayMins);
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }while (retryTimes++<maxRetryTimes);
        throw new LinkSteadyException("添加消息任务推送错误");
    }

    @Override
    public String getCorpId() {
        SysInfoBo sysInfoBo = commonFunService.getSysInfoByCode(CommonConstant.QYWX_CODE);
        if(null==sysInfoBo||StringUtils.isEmpty(sysInfoBo.getSysDomain()))
        {
            return "";
        }
        String url=sysInfoBo.getSysDomain()+"/api/getCorpId";
        String result= OkHttpUtil.getRequest(url);
        return result;
    }

    @Override
    public String getMpAppId() {
        SysInfoBo sysInfoBo = commonFunService.getSysInfoByCode(CommonConstant.QYWX_CODE);
        if(null==sysInfoBo||StringUtils.isEmpty(sysInfoBo.getSysDomain()))
        {
            return "";
        }
        String url=sysInfoBo.getSysDomain()+"/api/getMpAppId";
        String result= OkHttpUtil.getRequest(url);
        return result;
    }
}
