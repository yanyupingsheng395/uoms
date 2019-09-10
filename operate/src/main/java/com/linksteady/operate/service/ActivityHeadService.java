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
    List<ActivityHead> getDataListOfPage(int start, int end, String name);
    /**
     * 获取数据记录总数
     * @param name
     * @return
     */
    int getDataCount(String name);

    /**
     * 更新活动数据
     * @param headId
     * @param startDt
     * @param endDt
     * @param dateRange
     */
    void updateData(String headId, String startDt, String endDt, String dateRange, String type);

    Map<String, Object> getDataById(String headId);

    /**
     * 活动头表新增数据
     * @param activityHead
     */
    Long addData(ActivityHead activityHead);

    ActivityHead findById(String headId);
}
