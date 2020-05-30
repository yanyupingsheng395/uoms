package com.linksteady.operate.service;

import com.linksteady.operate.domain.MsgInfo;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/16
 */
public interface MsgService {

    List<MsgInfo> getMsgList();

    List<MsgInfo> getMsgPageList(String msgLevel, String readFlag, int limit, int offset);

    int getDataCount(String msgLevel, String readFlag);

    void updateMsgRead(Long msgId);

    void updateAllMsgRead();

}
