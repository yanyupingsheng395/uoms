package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.domain.ActivityPersonal;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivityPlanEffect;
import com.linksteady.operate.vo.ActivityContentVO;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface ActivityPushMapper {

    /**
     * 删除文案临时表
     */
    void deleteContentTmp(@Param("planId")  Long planId);

    /**
     * 获取此活动上配置的所有模板
     */
    List<Map<String,String>> getAllTemplate(Long headId,String activityStage,String activityType);

    List<ActivityDetail> getPushList(int limit, int offset, Long  planId);

    int getPushCount(Long planId);


//    /**
//     * 更新状态
//     * @param planId
//     * @param status
//     * @param version
//     * @return  返回受影响的记录的条数
//     */
//    int updateStatus(Long planId, String status, int version);
//
//    void deleteData(Long headId);

    /**
     * 将文案内容写入临时表
     * @param targetList
     */
    void insertPushContentTemp(@Param("list") List<ActivityContentVO> targetList);

    /**
     * 将临时表的文案更新到正式表
     */
    void updatePushContentFromTemp(@Param("planId")  Long planId);


    /**
     * 更新状态
     * @param planId
     * @param status
     * @param version
     * @return  返回受影响的记录的条数
     */
    int updateStatus(Long planId, String status,int version);


    /**
     * 更新推送状态
     */
    void updatePushScheduleDate(@Param("planId")  Long planId);


    /**
     * 将活动的推送数据写入到推送通道表中
     * @param planId
     */
    void insertToPushListLarge(Long planId);

    int validateNotifySms(long headId,String activityStage,String activityType);

    /**
     * 更新推送方式和推送时段
     */
    void updatePushMethod(Long planId, String pushMethod, String pushPeriod);

    void deletePlanGroup(long planId);

    void insertActivityPlanGroup(long planId);

    void updateActivityPlanGroup(long planId);

    void updateActivityPlanGroup2(long planId,String activityStage,String planType);

}
