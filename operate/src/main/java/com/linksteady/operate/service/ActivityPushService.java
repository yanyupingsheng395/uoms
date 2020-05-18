package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityPlan;

/**
 * @author hxcao
 * @date 2019-11-02
 */
public interface ActivityPushService {

    /**
     * 对活动运营的文案进行转换
     */
    void transActivityDetail(ActivityPlan activityPlan) throws Exception;

    /**
     * 获取 生成文案的锁
     */
    boolean getTransActivityContentLock(Long headId);

    /**
     * 删除 生成文案的锁
     */
    void delTransLock();

    /**
     * 初始化成长组
     */
    void initPlanGroup(ActivityPlan activityPlan);

    /**
     * 对活动计划进行推送
     * @param pushMethod
     * @param pushPeriod
     * @param activityPlan
     * @throws Exception
     */
    void pushActivity(String pushMethod, String pushPeriod, ActivityPlan activityPlan) throws Exception;

    void updatePlanToStop(ActivityPlan activityPlan) throws Exception;

    /**
     * 对活动文案进行配置
     */
    boolean validateNotifySms(ActivityPlan activityPlan);
}
