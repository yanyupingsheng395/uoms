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

    void pushActivity(String pushMethod, String pushPeriod, ActivityPlan activityPlan) throws Exception;

    void updatePlanToStop(ActivityPlan activityPlan) throws Exception;

    /**
     * 对活动文案进行配置
     */
    boolean validateNotifySms(ActivityPlan activityPlan);
}
