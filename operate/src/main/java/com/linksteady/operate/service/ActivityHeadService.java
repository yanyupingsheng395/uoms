package com.linksteady.operate.service;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivityTemplate;
import com.sun.corba.se.spi.ior.ObjectKey;

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

    void deleteData(String headId);

    int getDeleteCount(String headId);

    /**
     * 根据headId和stage获取计划的状态
     * @param headId
     * @param stage
     * @return
     */
    String getStatus(String headId, String stage);

    void updateStatus(String headId, String stage, String status);

    void changeAndUpdateStatus(String headId, String stage);
}
