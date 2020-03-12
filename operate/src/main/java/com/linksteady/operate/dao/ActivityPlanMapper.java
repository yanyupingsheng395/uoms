package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityPersonal;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivityPlanEffect;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface ActivityPlanMapper {

    void savePlanList(List<ActivityPlan> planList);

    List<ActivityPlan> getPlanList(Long headId);

    ActivityPlan getPlanInfo(Long planId);

    /**
     * 更新状态
     * @param planId
     * @param status
     * @param version
     * @return  返回受影响的记录的条数
     */
    int updateStatus(Long planId, String status,int version);

    void deleteData(Long headId);

    /**
     * 将活动的推送数据写入到推送通道表中
     * @param planId
     */
    void insertToPushListLarge(Long planId);

    /**
     * 获取群组的统计信息
     * @param planId
     * @return
     */
    List<Map<String,Object>> getUserGroupList(Long planId);

    /**
     * 获取此活动上配置的所有模板
     */
    List<Map<String,String>> getAllTemplate(Long headId,String activityStage,String activityType);

    /**
     * 更新推送方式和推送时段
     */
    void updatePushMethod(Long planId,String pushMethod,String pushPeriod);

    /**
     * 执行计划效果累计数据
     */
    ActivityPlanEffect selectPlanEffect(Long planId);

    /**
     * 效果的按天数据
     */
    List<ActivityPlanEffect> getPlanEffectStatisList(Long planId);

    List<ActivityPersonal> getPersonalPlanEffect(int start, int end, Long planId);

    int getDailyPersonalEffectCount(Long planId);
}
