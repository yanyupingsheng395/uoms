package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityPersonal;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivityPlanEffect;
import com.linksteady.operate.vo.ActivityGroupVO;
import com.linksteady.operate.vo.SmsStatisVO;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface QywxActivityPlanMapper {

    void savePlanList(List<ActivityPlan> planList);

    List<ActivityPlan> getPlanList(Long headId);

    ActivityPlan getPlanInfo(Long planId);

    void deletePlan(Long headId);

    /**
     * 获取群组的统计信息
     * @param planId
     * @return
     */
    List<ActivityGroupVO> getPlanGroupList(Long planId);

    /**
     * 执行计划效果累计数据
     */
    ActivityPlanEffect selectPlanEffect(Long planId);

    /**
     * 效果的按天数据
     */
    List<ActivityPlanEffect> getPlanEffectStatisList(Long planId);

    List<ActivityPersonal> getPersonalPlanEffect(int limit, int offset, Long planId);

    int getDailyPersonalEffectCount(Long planId);

    void expireActivityPlan();

    String getPlanStatus(String headId);
}
