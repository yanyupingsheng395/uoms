package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityPersonal;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.QywxActivityPlan;
import com.linksteady.operate.vo.ActivityGroupVO;
import com.linksteady.operate.vo.ActivityPlanEffectVO;
import com.linksteady.operate.vo.SmsStatisVO;
import org.apache.thrift.TException;

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
    void savePlanList(Long headId,String type);

    /**
     * 获取执行计划数据
     * @param headId
     * @return
     */
    List<QywxActivityPlan> getPlanList(Long headId);

    /**
     * 获取群组的统计信息
     * @param planId
     * @return
     */
    List<ActivityGroupVO> getPlanGroupList(Long planId);

    /**
     * 获取执行计划
     * @param planId
     * @return
     */
    QywxActivityPlan getPlanInfo(Long planId);


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

    String getPlanStatus(String headId);

    long calculationList(Long headId, Long planId) throws TException;

    boolean getTransLock(Long headId);

    void delTransLock();
}
