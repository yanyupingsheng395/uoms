package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.domain.ActivityPlan;
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
    void savePlanList(String headId, String hasPreheat);

    /**
     * 获取执行计划数据
     * @param headId
     * @return
     */
    List<ActivityPlan> getPlanList(String headId);

    /**
     * 更新状态
     * @param headId
     * @param planDateWid
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
     * 根据状态获取条数
     * @param headId
     * @param stage
     * @param asList
     * @return
     */
    int getStatusCount(String headId, String stage, List<String> asList);

    /**
     * 获取群组的统计信息
     * @param headId
     * @param planDtWid
     * @return
     */
    List<Map<String,Object>> getUserGroupList(String headId, String planDtWid);

    /**
     * 获取执行计划
     * @param headId
     * @return
     */
    ActivityPlan getPlanInfo(String headId,String planDtWid);

    /**
     * 对活动运营的文案进行转换
     */
    String transActivityDetail(String headId,String planDtWid);

    /**
     * 对变量进行替换
     * @param list
     * @return
     */
    List<ActivityContentVO> processVariable(List<ActivityDetail> list, Map<String,String> templateMap);

    void pushActivity(String headId, String planDateWid,String pushMethod,String pushPeriod, ActivityPlan activityPlan) throws Exception;
}
