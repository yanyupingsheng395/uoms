package com.linksteady.operate.service.impl;
import com.linksteady.common.domain.User;
import com.linksteady.operate.dao.MsgMapper;
import com.linksteady.operate.domain.MsgInfo;
import com.linksteady.operate.service.MsgService;
import org.apache.catalina.security.SecurityUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<MsgInfo> getMsgPageList(String typeCode, String readFlag, int start, int end) {
        return msgMapper.getMsgPageList(typeCode, readFlag, start, end);
    }

    @Override
    public int getDataCount(String typeCode, String readFlag) {
        return msgMapper.getDataCount(typeCode, readFlag);
    }

    @Override
    public void updateMsgRead(String msgId) {
        String userName = ((User)SecurityUtils.getSubject().getPrincipal()).getUsername();
        msgMapper.updateMsgRead(msgId, userName);
    }
}
