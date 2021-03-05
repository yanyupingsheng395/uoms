package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.constant.CommonConstant;
import com.linksteady.common.domain.QywxMessage;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.QywxChatBatchMsgMapper;
import com.linksteady.qywx.dao.QywxParamMapper;
import com.linksteady.qywx.domain.QywxChatBatchMsg;
import com.linksteady.qywx.domain.QywxParam;
import com.linksteady.qywx.service.QywxChatBatchMsgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class QywxChatBatchMsgServiceImpl implements QywxChatBatchMsgService {

    @Autowired
    private QywxChatBatchMsgMapper qywxChatBatchMsgMapper;

    @Autowired
    QywxParamMapper qywxParamMapper;

    private static final int maxRetryTimes=3;

    @Autowired
    private CommonFunService commonFunService;

    @Override
    public int getCount() {
        return qywxChatBatchMsgMapper.getCount();
    }

    @Override
    public List<QywxChatBatchMsg> getDataList(int limit, int offset) {
        return qywxChatBatchMsgMapper.getDataList(limit, offset);
    }

    @Override
    public void saveData(QywxChatBatchMsg qywxChatBatchMsg) {
        qywxChatBatchMsgMapper.saveData(qywxChatBatchMsg);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(long id) {
        qywxChatBatchMsgMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized String pushMessage(long batchMsgId) throws Exception {

        QywxChatBatchMsg qywxChatBatchMsg=qywxChatBatchMsgMapper.getChatBatchmsg(batchMsgId);
        if(qywxChatBatchMsg==null){
            log.info("群发消息失败，未获取到数据，{}",batchMsgId);
            throw new Exception("群发消息失败，未获取到数据");
        }

        QywxMessage qywxMessage=new QywxMessage();
        if("miniprogram".equals(qywxChatBatchMsg.getMsgType())){
            QywxParam qywxParam= qywxParamMapper.getQywxParam();
            String appId =qywxParam.getMpAppId();
            qywxMessage.setMpPicMediaId(qywxChatBatchMsg.getMpMediaId());
            qywxMessage.setMpAppid(appId);
            qywxMessage.setMpPage(qywxChatBatchMsg.getMpUrl());
            qywxMessage.setMpTitle(qywxChatBatchMsg.getMpTitle());
        }else if("image".equals(qywxChatBatchMsg.getMsgType())){
            qywxMessage.setImgPicUrl(qywxChatBatchMsg.getPicUrl());
        }else if("web".equals(qywxChatBatchMsg.getMsgType())){
            qywxMessage.setLinkDesc(qywxChatBatchMsg.getLinkDesc());
            qywxMessage.setLinkPicUrl(qywxChatBatchMsg.getLinkPicUrl());
            qywxMessage.setLinkTitle(qywxChatBatchMsg.getLinkTitle());
            qywxMessage.setLinkUrl(qywxChatBatchMsg.getLinkUrl());
        }

        //获取当前的群主数
        List<String> ownerList = Arrays.asList(qywxChatBatchMsg.getChatOwnerList().split(","));
        List<String> flagList=new ArrayList<>();
        //通过群主ID，获取当前群主下所有群成员的ID
        for (int i = 0; i < ownerList.size(); i++) {
            List<String> useridList=qywxChatBatchMsgMapper.getUserID(ownerList.get(i));
            //调用推送接口
            String result = pushQywxMessage(qywxMessage, ownerList.get(i), useridList);
            //处理返回
            if(StringUtils.isEmpty(result)){
                flagList.add("N");
            }else{
                flagList.add("Y");
            }
        }
        String status="done";
        if(flagList.contains("N")){
            status="fail";
        }
        UserBo userBo = (UserBo) SecurityUtils.getSubject().getPrincipal();
        qywxChatBatchMsgMapper.upadteStatus(batchMsgId,userBo.getUsername(),status);
        return status;
    }


    private String pushQywxMessage(QywxMessage message, String sender, List<String> externalUserList){
        int retryTimes=0;
        //返回结果值
        String result="";
        do{
            //构造推送参数
            JSONObject param=new JSONObject();
            param.put("chat_type","single");
            param.put("external_userid", JSONArray.parseArray(JSON.toJSONString(externalUserList)));
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
            url.append(WxPathConsts.QywxApiPathConstants.SEND_QYWX_MESSAGE);
            try {
                log.info("微信传入参数{}",param.toJSONString());
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
                if(jsonObject.getIntValue("errcode")!=0){
                    log.info("调用微信发送消息失败！{}",jsonObject.getString("errmsg"));
                    throw new Exception("调用微信发送消息失败！");
                }
                return result;
            } catch (Exception e) {
                log.error("调用企业微信接口发送消息失败，即将进行重试");
                if(retryTimes+1>maxRetryTimes){
                    //超过最大重试次数，写log日志，将错误信息返回上一层处理
                    log.warn("推送消息重试达到最大次数，接收到的参数为{}",message,sender,externalUserList.toString());
                    //todo 暂时未实现 返回空字符串 由上层进行处理
                    return "";
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
}
