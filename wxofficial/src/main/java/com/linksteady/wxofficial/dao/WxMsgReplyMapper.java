package com.linksteady.wxofficial.dao;

import com.linksteady.wxofficial.entity.po.WxFollowReply;
import com.linksteady.wxofficial.entity.po.WxMsgReply;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/24
 */
public interface WxMsgReplyMapper {

    void saveData(WxMsgReply wxMsgReply);

    List<WxMsgReply> getDataList();

    WxMsgReply getDataById(String id);

    void deleteById(String id);

    void updateData(WxMsgReply wxMsgReply);
}
