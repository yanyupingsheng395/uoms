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
            log.info("??????????????????????????????????????????{}",batchMsgId);
            throw new Exception("???????????????????????????????????????");
        }

        //????????????????????????
        List<String> ownerList = Arrays.asList(qywxChatBatchMsg.getChatOwnerList().split(","));
        //????????????ID??????????????????????????????????????????ID
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
            //setfollowuserid?????????ID
            qywxPushList.setFollowUserId(ownerList.get(i));
            //????????????ID??????????????????????????????ID
            List<String> chatidList=qywxChatBatchMsgMapper.getChatID(ownerList.get(i));
            qywxPushList.setExternalContactIds(org.apache.commons.lang3.StringUtils.join(chatidList.stream().collect(Collectors.toList()),","));

            //??????????????????uo_qywx_push_list?????????
            qywxChatBatchMsgMapper.insertPushList(qywxPushList);
            //????????????
            pushQywxMessage(qywxPushList,qywxChatBatchMsg,ownerList.get(i));
        }
    }

    private void pushQywxMessage(QywxPushList qywxPushList,QywxChatBatchMsg qywxChatBatchMsg,String sender){

        QywxMessage qywxMessage=new QywxMessage();
        //????????????
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
        String remark="????????????????????????";
        //??????????????????
        String result = pushQywxMessage(qywxMessage, sender,"group",null);
        //????????????
        if(StringUtils.isEmpty(result)) {
            status="F";
            remark="?????????????????????????????????";
        }else{
            JSONObject jsonObject = JSON.parseObject(result);
            msgId = jsonObject.getString("msgid");
            int errcode = jsonObject.getIntValue("errcode");
            failList = jsonObject.getString("fail_list");

            if(errcode!=0){
                status="F";
                remark="??????????????????????????????";
            }
        }
        //???????????????
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
     * @param message  ?????????????????????
     * @param sender    ??????????????????
     * @param chatType  ?????????single???????????????????????????group???????????????????????????
     * @param externalUserList  ????????????????????????id???????????????chatType???single?????????
     * @return
     */
    private String pushQywxMessage(QywxMessage message, String sender,String chatType, List<String> externalUserList){
        int retryTimes=0;
        //???????????????
        String result="";
        do{
            //??????????????????
            JSONObject param=new JSONObject();
            param.put("chat_type",chatType);

            if("single".equals(chatType)){
                param.put("external_userid", JSONArray.parseArray(JSON.toJSONString(externalUserList)));
            }
            param.put("sender",sender);

            JSONObject tempContent=null;
            //??????
            if(StringUtils.isNotEmpty(message.getText())){
                tempContent=new JSONObject();
                tempContent.put("content",message.getText());
                param.put("text",tempContent);
            }

            //??????
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
            //??????
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

            //?????????
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
                log.error("??????????????????????????????");
                return result;
            }
            StringBuffer url=new StringBuffer(sysInfoBo.getSysDomain());
            url.append(WxPathConsts.QywxApiPathConstants.SEND_QYWX_MESSAGE);
            try {
                log.info("??????????????????{}",param.toJSONString());
                result= OkHttpUtil.postRequestByJson(url.toString(),param.toJSONString());
                log.info("???????????????????????????{}???",result);
                if(org.springframework.util.StringUtils.isEmpty(result))
                {
                    throw new Exception("??????????????????????????????");
                }
                JSONObject jsonObject = JSON.parseObject(result);
                if(jsonObject.getIntValue("errcode")==-1)
                {
                    throw new Exception("????????????????????????????????????");
                }
                if(jsonObject.getIntValue("errcode")!=0){
                    log.info("?????????????????????????????????{}",jsonObject.getString("errmsg"));
                    throw new Exception("?????????????????????????????????");
                }
                return result;
            } catch (Exception e) {
                log.error("???????????????????????????????????????????????????????????????");
                if(retryTimes+1>maxRetryTimes){
                    //??????????????????????????????log?????????????????????????????????????????????
                   // log.warn("????????????????????????????????????????????????????????????{}",message,sender,externalUserList.toString());
                    //todo ??????????????? ?????????????????? ?????????????????????
                    return "";
                }else{
                    //?????????????????????????????????
                    long delayMins=1000* (1 << retryTimes);
                    log.debug("?????????????????????????????????{}???????????????{}?????????",delayMins,retryTimes);
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
