package com.linksteady.operate.service;

/**
 * 企业微信拉新调度任务服务类
 */
public interface AddUserJobService {

    /**
     * 主动拉新每日更新状态
     */
    void updateTriggerStatus();

    /**
     * 主动拉新计算效果
     */
    void updateTriggerEffect();

    /**
     * 被动拉新每日更新状态
     */
    void updateAddUserStatus();

    /**
     * 被动拉新计算效果
     */
    void updateAddUserEffect();

    /**
     * 每日订单进入 自动处理
     */
    void processDailyOrders()  throws Exception;
}
