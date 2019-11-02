package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityHead;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface ActivityHeadMapper {

    List<ActivityHead> getDataListOfPage(int start, int end, String name);

    int getDataCount(@Param("name") String name);

    void saveActivityHead(ActivityHead activityHead);

    void updateActiveHead(ActivityHead activityHead);

    ActivityHead findById(String headId);
}
