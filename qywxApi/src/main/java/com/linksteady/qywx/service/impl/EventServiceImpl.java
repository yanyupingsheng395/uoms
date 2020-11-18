package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.domain.ExternalContact;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.*;
import com.linksteady.qywx.vo.WxXmlMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public void handlerEvent(WxXmlMessage inMessage) throws WxErrorException {
       if("event".equals(inMessage.getMsgType())&&"change_external_contact".equals(inMessage.getEvent()))
       {

           if(StringUtils.equalsAnyIgnoreCase(inMessage.getChangeType(),"add_external_contact","edit_external_contact","add_half_external_contact"))
           {
                //添加联系人事件
               //更新外部联系人信息 并持久化到数据库
               ExternalContact externalContact=externalContactService.getExternalContractDetail(inMessage.getUserId(),inMessage.getExternalUserId());
               //保存外部客户ID 如果存在，什么也不做，如果不存在则insert
               externalContactService.saveExternalUserId(inMessage.getUserId(),inMessage.getExternalUserId());
               //保存外部客户详细信息
               externalContactService.updateExternalContract(externalContact);

               //(前提是已经开启了欢迎语) 如果是添加时，需要发送欢迎信息，则在此进行发送
               if("Y".equals(qywxService.getEnableWelcome())&&"add_external_contact".equals(inMessage.getChangeType())&&StringUtils.isNotEmpty(inMessage.getWelcomeCode()))
               {
                   try {
                       log.info("对{}发送欢迎语，收到的welcomeCode为{}",inMessage.getExternalUserId(),inMessage.getWelcomeCode());
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
               //删除外部联系人 删除跟进成员 则从本地库中进行删除。
               externalContactService.deleteExternalContract(
                       inMessage.getUserId(),
                       inMessage.getExternalUserId());

               //删除匹配信息
               mappingService.deleteMappingInfo(inMessage.getUserId(),inMessage.getExternalUserId());
           }else if("transfer_fail".equals(inMessage.getChangeType()))
           {
              //客户接替失败事件

           }else
           {
               log.info("未处理的外部联系人事件，事件类型{}",inMessage.getChangeType());
           }
       }else if("event".equals(inMessage.getMsgType())&&"change_external_chat".equals(inMessage.getEvent()))
       {
           //客户群变更事件
       }
    }
}
