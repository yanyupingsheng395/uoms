package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivityTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface ActivityPlanMapper {

    void savePlanList(List<ActivityPlan> planList);

    List<ActivityPlan> getPlanList(String headId);

    String getStatus(String headId, String planDateWid);

    void updateStatus(String headId, String planDateWid, String status);
}
