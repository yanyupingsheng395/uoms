package com.linksteady.operate.service;

import com.linksteady.operate.domain.AddUserHead;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/7/16
 */
public interface AddUserTriggerService {

    int getHeadCount();

    List<AddUserHead> getHeadPageList(int limit, int offset);

    /**
     * 对主记录进行保存
     * @param addUserHead
     */
    void saveData(AddUserHead addUserHead);

    void deleteTask(String id);

    List<Map<String, String>> getSource();

    AddUserHead getHeadById(long id);
    /**
     * 执行任务
     * @param headId
     */
    void execTask(long headId, String opUserName) throws Exception;

    void updateSmsContentAndContactWay(String headId, String smsContent, String contactWayId, String contactWayUrl, String isSourceName, String isProdName);

    Map<String, Object> getTaskResultData(String headId);

    List<Map<String, Object>> getStatisApplyData(String headId, String scheduleId);

    /**
     * 任务是否存在执行计划
     */
    int getScheduleCount(long headId);

    /**
     * 更新推送状态 （每日调度任务用)
     */
    void autoUpdateStatus();

    /**
     * 计算任务的效果 (每日调度任务用)
     */
    void calculateAddUserEffect();

    Map<Long, Object> geRegionData();

    String getStatus(String headId);

    List<String> getStatusList();
}
