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
import com.linksteady.operate.constant.QywxApiPathConstants;
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
    public String pushQywxMessage(QywxMessage message, String sender, List<String> externalUserList){
        int retryTimes=0;
        //返回结果值
        String result="";
        do{
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
            SysInfoBo sysInfoBo = commonFunService.getSysInfoByCode(CommonConstant.QYWX_CODE);
            if(null==sysInfoBo||StringUtils.isEmpty(sysInfoBo.getSysDomain())){
                log.error("企业微信应用未配置！");
              return result;
            }
            StringBuffer url=new StringBuffer(sysInfoBo.getSysDomain());
            url.append(QywxApiPathConstants.SEND_QYWX_MESSAGE);
            try {
                result= OkHttpUtil.postRequestByJson(url.toString(),param.toJSONString());
                log.info("微信返回接口数据【{}】",result);
                if(org.springframework.util.StringUtils.isEmpty(result))
                {
                    throw new Exception("企业微信接口返回为空");
                }
                JSONObject jsonObject = JSON.parseObject(result);
                if(jsonObject.getIntValue("errcode")==-1)
                {
                    throw new Exception("微信端返回繁忙，进行重试");
                }

                return result;
            } catch (Exception e) {
                log.error("调用企业微信接口发送消息失败，即将进行重试");
                if(retryTimes+1>maxRetryTimes){
                    //超过最大重试次数，写log日志，将错误信息返回上一层处理
                    log.warn("推送消息重试达到最大次数，接收到的参数为{}",message,sender,externalUserList.toString());
                    //todo 暂时未实现 返回空字符串 由上层进行处理
                }else{
                    //线程休眠，进入重试模式
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
        return result;
    }

    @Override
    public String getCorpId() {
        SysInfoBo sysInfoBo = commonFunService.getSysInfoByCode(CommonConstant.QYWX_CODE);
        if(null==sysInfoBo||StringUtils.isEmpty(sysInfoBo.getSysDomain()))
        {
            return "";
        }
        String url=sysInfoBo.getSysDomain()+QywxApiPathConstants.GET_CORP_ID;
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
        String url=sysInfoBo.getSysDomain()+QywxApiPathConstants.GET_MP_APPID;
        String result= OkHttpUtil.getRequest(url);
        return result;
    }
}
