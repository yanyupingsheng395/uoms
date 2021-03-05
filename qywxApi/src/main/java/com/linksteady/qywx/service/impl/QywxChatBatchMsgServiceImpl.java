package com.linksteady.qywx.service.impl;

import com.linksteady.common.domain.QywxMessage;
import com.linksteady.qywx.dao.QywxChatBatchMsgMapper;
import com.linksteady.qywx.dao.QywxParamMapper;
import com.linksteady.qywx.domain.QywxChatBatchMsg;
import com.linksteady.qywx.domain.QywxParam;
import com.linksteady.qywx.service.QywxChatBatchMsgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class QywxChatBatchMsgServiceImpl implements QywxChatBatchMsgService {

    @Autowired
    private QywxChatBatchMsgMapper qywxChatBatchMsgMapper;

    @Autowired
    QywxParamMapper qywxParamMapper;

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
        return null;
    }
}
