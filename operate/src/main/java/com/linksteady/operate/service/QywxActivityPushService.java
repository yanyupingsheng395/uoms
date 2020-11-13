package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.QywxActivityPlan;

/**
 * @author hxcao
 * @date 2019-11-02
 */
public interface QywxActivityPushService {

    /**
     * 对活动运营的文案进行转换
     */
    void transActivityDetail(QywxActivityPlan activityPlan) throws Exception;

    /**
     * 获取 生成文案的锁
     */
    boolean getTransActivityContentLock(Long headId);

    /**
     * 删除 生成文案的锁
     */
    void delTransLock();

    /**
     * 对活动计划进行推送
     * @param activityPlan
     * @throws Exception
     */
    void pushActivity(QywxActivityPlan activityPlan) throws Exception;

    void updatePlanToStop(QywxActivityPlan activityPlan) throws Exception;

    /**
     * 对活动文案的配置进行校验
     */
    boolean validateSmsConfig(QywxActivityPlan activityPlan);
}
