package com.linksteady.operate.dao;

import com.linksteady.common.domain.ResponseBo;
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
public interface ActivityHeadMapper {

    List<ActivityHead> getDataListOfPage(int start, int end, String name, String date, String status);

    int getDataCount(@Param("name") String name);

    void saveActivityHead(ActivityHead activityHead);

    void updateActiveHead(ActivityHead activityHead);

    ActivityHead findById(String headId);

    List<ActivityTemplate> getTemplateTableData();

    String getActivityName(String headId);

    int getActivityStatus(String id);

    String getPreheatStatus(String headId);

    void updateGroupChanged(String time, String headId, String stage, String changedStatus);

    Map<String, String> getDataChangedStatus(String headId, String stage);

    Map<String, Date> getStageDate(String headId);

    /**
     * 更新头表预售状态
     * @param headId
     */
    void updatePreheatStatusHead(String headId,String status);

    /**
     * 更新头表正式状态
     * @param headId
     */
    void updateFormalStatusHead(String headId,String status);

    void deleteActivity(String headId);

    int getDeleteCount(String headId);

    String getStatus(@Param("sql") String sql);
}
