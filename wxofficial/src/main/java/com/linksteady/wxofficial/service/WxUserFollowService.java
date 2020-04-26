package com.linksteady.wxofficial.service;

import com.linksteady.wxofficial.entity.po.WxFollowReply;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/24
 */
public interface WxUserFollowService {

    void saveData(WxFollowReply wxFollowReply);

    List<WxFollowReply> getDataList();

    WxFollowReply getDataById(String id) throws UnsupportedEncodingException;

    void deleteById(String id);

    void updateData(WxFollowReply wxFollowReply);
}
