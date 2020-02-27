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

    ActivityPlan getPlanInfo(String headId, String planDateWid);

    /**
     * 更新状态
     * @param headId
     * @param planDateWid
     * @param status
     * @param version
     * @return  返回受影响的记录的条数
     */
    int updateStatus(String headId, String planDateWid, String status,int version);

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


    /**
     * 获取群组的统计信息
     * @param headId
     * @param planDtWid
     * @return
     */
    List<Map<String,Object>> getUserGroupList(String headId, String planDtWid);

    /**
     * 获取活动上配置的所有模板
     */
    List<Map<String,String>> getAllTemplate(String headId);

    /**
     * 更新推送方式和推送时段
     */
    void updatePushMethod(String headId,String planDateWid,String pushMethod,String pushPeriod);
}
