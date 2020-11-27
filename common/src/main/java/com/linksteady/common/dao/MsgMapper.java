package com.linksteady.common.dao;

import com.linksteady.common.domain.MsgInfo;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/16
 */
public interface MsgMapper {

    List<MsgInfo> getMsgList();

    int getDataCount(String msgLevel, String readFlag);

    List<MsgInfo> getMsgPageList(String msgLevel, String readFlag, int limit, int offset);

    void updateMsgRead(Long msgId, String userName);

    void updateAllMsgRead(String userName);
}
