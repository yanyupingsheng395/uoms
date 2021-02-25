package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.domain.ExternalContact;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.*;
import com.linksteady.qywx.vo.WxXmlMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

    @Autowired
    ExternalContactService externalContactService;

    @Autowired
    QywxService qywxService;

    @Autowired
    WelcomeService welcomeService;

    @Autowired
    MappingService mappingService;

    @Autowired
    CustomerBaseService customerBaseService;

    @Autowired
    QywxTagService qywxTagService;

    @Override
    public void handlerEvent(WxXmlMessage inMessage) throws WxErrorException {
        log.info("事件消息内容:{}", inMessage.toString());
       if("event".equals(inMessage.getMsgType())&&"change_external_contact".equals(inMessage.getEvent()))
       {
           log.info("");
           if(StringUtils.equalsAnyIgnoreCase(inMessage.getChangeType(),"add_external_contact","edit_external_contact","add_half_external_contact"))
           {
               log.info("外部联系人事件-新增、编辑外部联系人");
                //添加联系人事件
               //更新外部联系人信息 并持久化到数据库
               ExternalContact externalContact=externalContactService.getExternalContractDetail(inMessage.getUserId(),inMessage.getExternalUserId());
               //保存外部客户ID 如果存在，什么也不做，如果不存在则insert
               externalContactService.saveExternalUserId(inMessage.getUserId(),inMessage.getExternalUserId());
               //保存外部客户详细信息
               externalContactService.updateExternalContract(externalContact);

               //(前提是已经开启了欢迎语) 如果是添加时，需要发送欢迎信息，则在此进行发送
               if("add_external_contact".equals(inMessage.getChangeType())&&StringUtils.isNotEmpty(inMessage.getWelcomeCode()))
               {
                   //获取欢迎语的导购白名单
                   Set<String> welcomeWhiteUserSet = qywxService.getWelcomeWhiteUserSet();

                   if("Y".equals(qywxService.getEnableWelcome())||welcomeWhiteUserSet.contains(inMessage.getUserId()))
                   try {
                       log.info("对{}发送欢迎语，收到的welcomeCode为{},欢迎语开关：{},当前导购是否在白名单:{}",inMessage.getExternalUserId(),inMessage.getWelcomeCode(),
                               "Y".equals(qywxService.getEnableWelcome()),welcomeWhiteUserSet.contains(inMessage.getUserId()));
                       welcomeService.sendWelcomeMessage(inMessage.getWelcomeCode(),inMessage.getExternalUserId());
                   } catch (Exception e) {
                       log.error("推送欢迎语失败！");
                   }
               }

               //进行用户匹配
               if(null!=externalContact&&StringUtils.isNotEmpty(externalContact.getUnionid()))
               {
                   mappingService.updateMappingInfo(inMessage.getUserId(),inMessage.getExternalUserId(),externalContact.getUnionid());
               }
           }else if(StringUtils.equalsAnyIgnoreCase(inMessage.getChangeType(),"del_external_contact","del_follow_user"))
           {
               log.info("外部联系人事件-删除外部联系人");
               //删除外部联系人 删除跟进成员 则从本地库中进行删除。
               externalContactService.deleteExternalContract(
                       inMessage.getUserId(),
                       inMessage.getExternalUserId());

               //删除匹配信息
               mappingService.deleteMappingInfo(inMessage.getUserId(),inMessage.getExternalUserId());
           }else if("transfer_fail".equals(inMessage.getChangeType())){
              //todo 客户接替失败事件

           }else{
               log.info("未处理的外部联系人事件，事件类型{}",inMessage.getChangeType());
           }
       }else if("event".equals(inMessage.getMsgType())&&"change_external_chat".equals(inMessage.getEvent())){
           String chatId = inMessage.getChatId();
           if(StringUtils.equalsAnyIgnoreCase(inMessage.getChangeType(),"create")){
               log.info("群事件-客户群创建");
               customerBaseService.saveChatBase(chatId,true);

           }else if(StringUtils.equalsAnyIgnoreCase(inMessage.getChangeType(),"update")){
               log.info("群事件-客户群变更");
               //客户群变更，先删除，后从企微处获取
               customerBaseService.updateChat(chatId);

           }else if(StringUtils.equalsAnyIgnoreCase(inMessage.getChangeType(),"dismiss")){
               log.info("客户群解散");
               customerBaseService.deleChatBase(chatId);
           }
       }else if("event".equals(inMessage.getMsgType())&&"change_external_tag".equals(inMessage.getEvent())){
           // TODO: 2021/1/6 标签事件只维护了标签的增删改查。标签组的事件操作未维护，原因是微信端未提供根据标签组ID去查标签组信息的接口
           String id=inMessage.getId();
            if(StringUtils.equalsAnyIgnoreCase(inMessage.getChangeType(),"create")){
                if("tag".equals(inMessage.getTagType())){
                    log.info("标签事件-标签创建");
                    qywxTagService.saveTag(id);
                }
            }else if(StringUtils.equalsAnyIgnoreCase(inMessage.getChangeType(),"update")){
                if("tag".equals(inMessage.getTagType())) {
                    log.info("标签事件-标签更新");
                    qywxTagService.updateTag(id);
                }
            }else if(StringUtils.equalsAnyIgnoreCase(inMessage.getChangeType(),"delete")){
                if("tag".equals(inMessage.getTagType())){
                    log.info("标签事件-标签删除");
                    qywxTagService.delTagGroupData(id,"T");
                }
            }
       }
    }
}
