package com.linksteady.operate.service;

import com.linksteady.operate.domain.MsgInfo;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/16
 */
public interface MsgService {

    List<MsgInfo> getMsgList();

    List<MsgInfo> getMsgPageList(String typeCode, String readFlag, int start, int end);

    int getDataCount(String typeCode, String readFlag);

    void updateMsgRead(String msgId);

    void updateAllMsgRead();

}
