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

    void deleteData(String headId);

    /**
     * 将活动的推送数据写入到推送通道表中
     * @param headId
     * @param planDateWid
     */
    void insertToPushListLarge(String headId, String planDateWid);

    /**
     * 将活动的推送数据标记为失败(停止)
     * @param headId
     * @param planDateWid
     */
    void updatePushListLargeToFaild(String headId,String planDateWid);

    int getStatusCount(String headId, String stage, List<String> asList);
}
