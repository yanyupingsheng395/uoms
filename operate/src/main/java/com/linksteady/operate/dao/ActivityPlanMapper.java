package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityPersonal;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivityPlanEffect;
import com.linksteady.operate.domain.ActivityPlanGroup;
import com.linksteady.operate.vo.SmsStatisVO;

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

    void deletePlan(Long headId);

    void deletePlanGroup(Long headId);

    /**
     * 获取群组的统计信息
     * @param planId
     * @return
     */
    List<ActivityPlanGroup> getPlanGroupList(Long planId);

    /**
     * 获取按短信内容的统计信息的总记录数
     * @param planId
     * @return
     */
    int getPlanSmsContentListCount(Long planId);

    /**
     * 获取按短信内容的统计信息
     * @param planId
     * @return
     */
    List<SmsStatisVO> getPlanSmsContentList(Long planId, int start, int end);


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

    void expireActivityPlan();

    String getPlanStatus(String headId, String stage);
}
