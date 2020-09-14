package com.linksteady.operate.service;

import com.linksteady.operate.domain.*;

import java.util.List;
import java.util.Map;

/**
 * @author huang
 * @date 2020-05-12
 */
public interface QywxDailyService {

    List<QywxDailyHeader> getHeadList(int limit, int offset, String taskDate);

    int getTotalCount(String touchDt);

    Map<String, Object> getTaskOverViewData(Long headId);

    Map<String, Object> getProdCountBySpu(Long headId,String spuName);

    Map<String, Object> getMatrixData(Long headId,String userValue);

    /**
     * 获取生成文案的锁  true表示加锁成功 false表示加锁失败
     */
    boolean getTransContentLock(String headId);

    /**
     * 释放生成文案的锁
     */
    void delTransLock();

    /**
     * 获取任务头信息
     */
    QywxDailyHeader getHeadInfo(Long headId);

    /**
     * 企业微信消息推送
     * @param qywxDailyHeader
     * @param effectDays
     * @throws Exception
     */
    void push(QywxDailyHeader qywxDailyHeader,Long effectDays) throws Exception;

    /**
     * 获取推送变化数据
     * @param headId
     * @return
     */
    Map<String, Object> getPushEffectChange(Long headId);

    QywxDailyStaffEffect getDailyStaffEffect(Long headId, String qywxUserId);
}
