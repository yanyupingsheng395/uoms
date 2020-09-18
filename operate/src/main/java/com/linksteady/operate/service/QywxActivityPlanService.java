package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityPersonal;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.vo.ActivityGroupVO;
import com.linksteady.operate.vo.ActivityPlanEffectVO;
import com.linksteady.operate.vo.SmsStatisVO;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-11-02
 */
public interface QywxActivityPlanService {
    /**
     * 生成plan数据
     */
    void savePlanList(Long headId, String stage, String type);

    /**
     * 获取执行计划数据
     * @param headId
     * @return
     */
    List<ActivityPlan> getPlanList(Long headId);

    /**
     * 获取群组的统计信息
     * @param planId
     * @return
     */
    List<ActivityGroupVO> getPlanGroupList(Long planId);

    /**
     * 获取按消息内容的统计信息的总行数
     */
   int getPlanSmsContentListCount(Long planId);

    /**
     * 获取按消息内容的统计信息
     */
    List<SmsStatisVO> getPlanSmsContentList(Long planId, int limit, int offset);

    /**
     * 获取执行计划
     * @param planId
     * @return
     */
    ActivityPlan getPlanInfo(Long planId);


    ActivityPlanEffectVO getPlanEffectById(Long planId, String kpiType);

    /**
     * 获取执行计划效果的趋势信息
     */
    Map<String, Object> getPlanEffectTrend(Long planId);

    List<ActivityPersonal> getPersonalPlanEffect(int limit, int offset, Long planId);

    int getDailyPersonalEffectCount(Long planId);

    /**
     * 活动运营 执行计划失效
     */
    void expireActivityPlan();

    String getPlanStatus(String headId, String stage);
}
