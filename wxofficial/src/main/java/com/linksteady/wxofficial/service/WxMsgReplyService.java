package com.linksteady.wxofficial.service;

import com.linksteady.wxofficial.entity.po.WxFollowReply;
import com.linksteady.wxofficial.entity.po.WxMsgReply;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/24
 */
public interface WxMsgReplyService {

    void saveData(WxMsgReply wxMsgReply);

    List<WxMsgReply> getDataList();

    WxMsgReply getDataById(String id) throws UnsupportedEncodingException;

    void deleteById(String id);

    void updateData(WxMsgReply wxMsgReply);
}
