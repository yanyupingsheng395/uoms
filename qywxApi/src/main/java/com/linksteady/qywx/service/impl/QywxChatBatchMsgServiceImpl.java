package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.QywxChatBatchMsgMapper;
import com.linksteady.qywx.domain.QywxChatBatchMsg;
import com.linksteady.qywx.service.QywxChatBatchMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QywxChatBatchMsgServiceImpl implements QywxChatBatchMsgService {

    @Autowired
    private QywxChatBatchMsgMapper qywxChatBatchMsgMapper;

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
}
