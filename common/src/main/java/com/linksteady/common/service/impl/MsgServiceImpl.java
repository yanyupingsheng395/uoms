package com.linksteady.common.service.impl;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.dao.MsgMapper;
import com.linksteady.common.domain.MsgInfo;
import com.linksteady.common.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.shiro.SecurityUtils.getSubject;

/**
 * @author hxcao
 * @date 2020/3/16
 */
@Service
public class MsgServiceImpl implements MsgService {

    @Autowired
    private MsgMapper msgMapper;

    @Override
    public List<MsgInfo> getMsgList() {
        return msgMapper.getMsgList();
    }

    @Override
    public List<MsgInfo> getMsgPageList(String msgLevel, String readFlag, int limit, int offset) {
        return msgMapper.getMsgPageList(msgLevel, readFlag, limit, offset);
    }

    @Override
    public int getDataCount(String msgLevel, String readFlag) {
        return msgMapper.getDataCount(msgLevel, readFlag);
    }

    @Override
    public void updateMsgRead(Long msgId) {
        String userName = ((UserBo) getSubject().getPrincipal()).getUsername();
        msgMapper.updateMsgRead(msgId, userName);
    }

    @Override
    public void updateAllMsgRead() {
        String userName = ((UserBo) getSubject().getPrincipal()).getUsername();
        msgMapper.updateAllMsgRead(userName);
    }
}
