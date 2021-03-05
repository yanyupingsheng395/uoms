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
import com.linksteady.qywx.domain.QywxPushList;
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
import java.util.stream.Collectors;

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
    public synchronized void pushMessage(long batchMsgId) throws Exception {
        QywxChatBatchMsg qywxChatBatchMsg=qywxChatBatchMsgMapper.getChatBatchmsg(batchMsgId);
        if(qywxChatBatchMsg==null){
            log.info("群发消息失败，未获取到数据，{}",batchMsgId);
            throw new Exception("群发消息失败，未获取到数据");
        }

        //获取当前的群主数
        List<String> ownerList = Arrays.asList(qywxChatBatchMsg.getChatOwnerList().split(","));
        //通过群主ID，获取当前群主下所有群成员的ID
        for (int i = 0; i < ownerList.size(); i++) {
            QywxPushList qywxPushList=new QywxPushList();
            qywxPushList.setTextContent(qywxChatBatchMsg.getTextContent());
            qywxPushList.setMpTitle(qywxChatBatchMsg.getMpTitle());
            qywxPushList.setMpUrl(qywxChatBatchMsg.getMpUrl());
            qywxPushList.setMpMediaId(qywxChatBatchMsg.getMpMediaId());
            qywxPushList.setMpAppid(qywxParamMapper.getQywxParam().getMpAppId());
            qywxPushList.setPicUrl(qywxChatBatchMsg.getPicUrl());
            qywxPushList.setLinkUrl(qywxChatBatchMsg.getLinkUrl());
            qywxPushList.setLinkDesc(qywxChatBatchMsg.getLinkDesc());
            qywxPushList.setLinkPicurl(qywxChatBatchMsg.getLinkPicUrl());
            qywxPushList.setLinkTitle(qywxChatBatchMsg.getLinkTitle());
            qywxPushList.setSourceCode("CHAT");
            //setfollowuserid为群主ID
            qywxPushList.setFollowUserId(ownerList.get(i));
            //根据群主ID，获取群主下面的群聊ID
            List<String> chatidList=qywxChatBatchMsgMapper.getChatID(ownerList.get(i));
            qywxPushList.setExternalContactIds(org.apache.commons.lang3.StringUtils.join(chatidList.stream().collect(Collectors.toList()),","));

            //将信息，放入uo_qywx_push_list表中。
            qywxChatBatchMsgMapper.insertPushList(qywxPushList);
            //执行推送
            pushQywxMessage(qywxPushList,qywxChatBatchMsg,ownerList.get(i));
        }
    }

    private void pushQywxMessage(QywxPushList qywxPushList,QywxChatBatchMsg qywxChatBatchMsg,String sender){

        QywxMessage qywxMessage=new QywxMessage();
        //填充文本
        if(StringUtils.isNotEmpty(qywxChatBatchMsg.getTextContent())){
            qywxMessage.setText(qywxChatBatchMsg.getTextContent());
        }

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

        String status="S";
        String msgId ="";
        String failList="";
        String remark="群发消息推送成功";
        //调用推送接口
        String result = pushQywxMessage(qywxMessage, sender,"group",null);
        //处理返回
        if(StringUtils.isEmpty(result)) {
            status="F";
            remark="调用企业微信接口返回空";
        }else{
            JSONObject jsonObject = JSON.parseObject(result);
            msgId = jsonObject.getString("msgid");
            int errcode = jsonObject.getIntValue("errcode");
            failList = jsonObject.getString("fail_list");

            if(errcode!=0){
                status="F";
                remark="调用企业微信接口失败";
            }
        }
        //更新状态。
        qywxChatBatchMsgMapper.updatePushList(qywxPushList.getPushId(),status,msgId,failList,remark);

        String flag="done";
        if("F".equals(status)){
            flag="fail";
        }
        UserBo userBo = (UserBo) SecurityUtils.getSubject().getPrincipal();
        qywxChatBatchMsgMapper.upadteStatus(qywxChatBatchMsg.getBatchMsgId(),userBo.getUsername(),flag);
    }

    /**
     *
     * @param message  推送消息的对象
     * @param sender    发消息的成员
     * @param chatType  类型（single，表示发送给客户，group表示发送给客户群）
     * @param externalUserList  客户的外部联系人id列表，仅在chatType为single时有效
     * @return
     */
    private String pushQywxMessage(QywxMessage message, String sender,String chatType, List<String> externalUserList){
        int retryTimes=0;
        //返回结果值
        String result="";
        do{
            //构造推送参数
            JSONObject param=new JSONObject();
            param.put("chat_type",chatType);

            if("single".equals(chatType)){
                param.put("external_userid", JSONArray.parseArray(JSON.toJSONString(externalUserList)));
            }
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
                   // log.warn("推送消息重试达到最大次数，接收到的参数为{}",message,sender,externalUserList.toString());
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
