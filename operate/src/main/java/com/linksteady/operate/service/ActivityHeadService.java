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
    List<ActivityHead> getDataListOfPage(int start, int end, String name);
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

    List<ActivityTemplate> getTemplateTableData();

    List<ActivityPlan> getPlanList(String headId);

    String getActivityName(String headId);

    int getActivityStatus(String id);

    void submitActivity(String headId, String stage);
}
