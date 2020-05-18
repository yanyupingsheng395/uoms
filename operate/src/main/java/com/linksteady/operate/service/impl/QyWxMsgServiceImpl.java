package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.QywxMapper;
import com.linksteady.operate.domain.QyWxMsg;
import com.linksteady.operate.service.QyWxMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/5/15
 */
@Service
public class QyWxMsgServiceImpl implements QyWxMsgService {

    @Autowired
    private QywxMapper qywxMapper;

    @Override
    public void saveData(QyWxMsg qyWxMsg) {
        qywxMapper.saveData(qyWxMsg);
    }

    @Override
    public List<QyWxMsg> getDataListPage(int limit, int offset) {
        return qywxMapper.getDataListPage(limit, offset);
    }

    @Override
    public int getTotalCount() {
        return qywxMapper.getTotalCount();
    }

    @Override
    public void updateQyWxMsg(QyWxMsg qyWxMsg) {
        qywxMapper.updateQyWxMsg(qyWxMsg);
    }

    @Override
    public void deleteDataById(String id) {
        qywxMapper.deleteDataById(id);
    }
}