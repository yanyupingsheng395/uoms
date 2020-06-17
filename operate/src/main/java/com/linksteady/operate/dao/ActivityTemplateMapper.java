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

    List<ActivityTemplate> getTemplateTableData(@Param("activityTemplate") ActivityTemplate activityTemplate);

    void setSmsCode(String groupId, String tmpCode, Long headId, String type, String stage);

    int checkTmpIsUsed(String tmpCode);

    void validUserGroup(Long headId, String stage);

    void removeSmsSelected(String type, String headId, String stage, String smsCode, String groupId);
}
