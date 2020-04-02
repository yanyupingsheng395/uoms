package com.linksteady.operate.dao;

import com.linksteady.operate.domain.MsgInfo;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/16
 */
public interface MsgMapper {

    List<MsgInfo> getMsgList();

    int getDataCount(String typeCode, String readFlag);

    List<MsgInfo> getMsgPageList(String typeCode, String readFlag, int start, int end);

    void updateMsgRead(String msgId, String userName);

    void updateAllMsgRead(String userName);
}
