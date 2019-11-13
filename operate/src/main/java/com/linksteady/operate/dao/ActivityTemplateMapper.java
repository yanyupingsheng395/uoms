package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-13
 */
public interface ActivityTemplateMapper {

    void saveTemplate(ActivityTemplate activityTemplate);

    void deleteTemplate(String code);

    ActivityTemplate getTemplate(String code);

    void update(ActivityTemplate activityTemplate);

    int getTotalCount(@Param("code") String code);

    List<ActivityTemplate> getTemplateList(int start, int end, String code);
}
