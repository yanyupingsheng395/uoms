package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityHead;

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
    ActivityHead findById(Long headId);

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

    Map<String, String> getDataChangedStatus(Long headId, String stage);

    void deleteData(Long headId);

    int getDeleteCount(Long headId);

    /**
     * 根据headId和stage获取计划的状态
     * @param headId
     * @param stage
     * @return
     */
    String getStatus(Long headId, String stage);

    void updateStatus(Long headId, String stage, String status, String type);
}
