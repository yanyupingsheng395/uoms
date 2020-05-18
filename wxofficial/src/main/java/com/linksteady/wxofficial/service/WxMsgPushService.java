package com.linksteady.wxofficial.service;

import com.linksteady.wxofficial.entity.po.WxPushHead;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/4/29
 */
public interface WxMsgPushService {
    int getCount();

    List<WxPushHead> getDataList(int limit, int offset);

    void saveData(WxPushHead wxPushHead);

    void deleteById(String id);

    void pushMsg(String pushMethod, String pushPeriod, String headId);

    WxPushHead getMsgHeadById(String headId);
}
