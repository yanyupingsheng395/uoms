package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.domain.ActivityPersonal;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivityPlanEffect;
import com.linksteady.operate.vo.ActivityContentVO;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-11-02
 */
public interface ActivityPlanService {
    /**
     * 生成plan数据
     * @param headId
     * @param hasPreheat
     */
    void savePlanList(Long headId, String hasPreheat);

    /**
     * 获取执行计划数据
     * @param headId
     * @return
     */
    List<ActivityPlan> getPlanList(Long headId);

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
     * 获取执行计划
     * @param planId
     * @return
     */
    ActivityPlan getPlanInfo(Long planId);

    /**
     * 对活动运营的文案进行转换
     */
    String transActivityDetail( ActivityPlan activityPlan);

    /**
     * 对变量进行替换
     * @param list
     * @return
     */
    List<ActivityContentVO> processVariable(List<ActivityDetail> list, Map<String,String> templateMap);

    void pushActivity(String pushMethod,String pushPeriod, ActivityPlan activityPlan) throws Exception;

    ActivityPlanEffect getPlanEffectById(Long planId);

    /**
     * 获取执行计划效果的趋势信息
     */
    Map<String, Object> getPlanEffectTrend(Long planId);

    List<ActivityPersonal> getPersonalPlanEffect(int start, int end, Long planId);

    int getDailyPersonalEffectCount(Long planId);
}
