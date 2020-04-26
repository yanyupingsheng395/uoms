package com.linksteady.wxofficial.dao;

import com.linksteady.wxofficial.entity.po.WxFollowReply;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/24
 */
public interface WxUserFollowMapper {

    void saveData(WxFollowReply wxFollowReply);

    List<WxFollowReply> getDataList();

    WxFollowReply getDataById(String id);

    void deleteById(String id);

    void updateData(WxFollowReply wxFollowReply);
}
