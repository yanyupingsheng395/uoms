package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.QywxMsgMapper;
import com.linksteady.operate.domain.QywxMsg;
import com.linksteady.operate.service.QywxMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/5/15
 */
@Service
public class QywxMsgServiceImpl implements QywxMsgService {

    @Autowired
    private QywxMsgMapper qywxMsgMapper;

    @Override
    public void saveData(QywxMsg qyWxMsg) {
        qywxMsgMapper.saveData(qyWxMsg);
    }

    @Override
    public List<QywxMsg> getDataListPage(int limit, int offset) {
        return qywxMsgMapper.getDataListPage(limit, offset);
    }

    @Override
    public int getTotalCount() {
        return qywxMsgMapper.getTotalCount();
    }

    @Override
    public void updateQyWxMsg(QywxMsg qyWxMsg) {
        qywxMsgMapper.updateQywxMsg(qyWxMsg);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDataById(String id) {
        qywxMsgMapper.deleteDataById(id);
        qywxMsgMapper.updateDataById(id);
    }

    @Override
    public QywxMsg getDataById(String id) {
        return qywxMsgMapper.getDataById(id);
    }

    @Override
    public void refreshDataById(String id) {
        qywxMsgMapper.refreshDataById(id);
    }
}