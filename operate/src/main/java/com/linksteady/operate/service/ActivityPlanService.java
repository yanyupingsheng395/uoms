package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.domain.ActivityPlan;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    void savePlanList(String headId, String hasPreheat);

    /**
     * 获取执行计划数据
     * @param headId
     * @return
     */
    List<ActivityPlan> getPlanList(String headId);

    /**
     * 获取计划的状态
     * @param headId
     * @param planDateWid
     * @return
     */
    String getStatus(String headId, String planDateWid);

    /**
     * 更新状态
     * @param headId
     * @param planDateWid
     */
    void updateStatus(String headId, String planDateWid, String status);
}
