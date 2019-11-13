package com.linksteady.operate.service;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivityTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface ActivityHeadService {

    /**
     * 获取头表的分页数据
     * @param start
     * @param end
     * @param name
     * @return
     */
    List<ActivityHead> getDataListOfPage(int start, int end, String name, String date, String status);
    /**
     * 获取数据记录总数
     * @param name
     * @return
     */
    int getDataCount(String name);

    /**
     * 插入头表
     * @param activityHead
     * @return
     */
    int saveActivityHead(ActivityHead activityHead);

    /**
     * 根据ID获取
     * @param headId
     * @return
     */
    ActivityHead findById(String headId);

    /**
     * 获取模板数据
     * @return
     */
    List<ActivityTemplate> getTemplateTableData();

    /**
     * 通过headId获取活动名称
     * @param headId
     * @return
     */
    String getActivityName(String headId);

    /**
     * 通过状态判断是否可以执行计划
     * @param id
     * @return
     */
    int getActivityStatus(String id);

    /**
     * 提交计划，更改活动阶段的状态
     * @param headId
     * @param stage
     */
    void submitActivity(String headId, String stage);

    Map<String, String> getDataChangedStatus(String headId, String stage);

    /**
     * 更新头表预售状态为执行中(plan表中有一条为执行)
     * @param headId
     */
    void updatePreheatHeadToDoing(String headId);

    /**
     * 更新头表正式状态为执行中(plan表中有一条为执行)
     * @param headId
     */
    void updateFormalHeadToDoing(String headId);
}
