package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyInfo;
import com.linksteady.operate.domain.DailyPushInfo;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyPushService {

    void generatePushList(String headerId);

    /**
     * 获取当前待发送的短信列表
     */
    List<DailyPushInfo> getSendSmsList();

    /**
     * 更新短信发送的状态
     *
     */
    void updateSendStatus(List<DailyPushInfo> list,String status);

    /**
     * 更新主记录为 完成推送，效果统计中
     */
    void updateHeaderToDone();

    /**
     * 更新主记录的触达统计信息
     */
    void updateHeaderSendStatis();

}
