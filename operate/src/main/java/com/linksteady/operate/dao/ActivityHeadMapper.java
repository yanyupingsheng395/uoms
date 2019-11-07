package com.linksteady.operate.dao;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivityTemplate;
import org.apache.ibatis.annotations.Param;

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

    List<ActivityPlan> getPlanList(String headId);

    String getActivityName(String headId);

    int getActivityStatus(String id);

    void submitActivity(@Param("sql") String sql);

    String getPreheatStatus(String headId);

    /**
     * 更新活动为大型活动 or 小型活动的 标记字段
     * @param headId
     */
    void updateActivityFlag(String headId);
}
