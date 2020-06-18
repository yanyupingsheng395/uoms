package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityTemplate;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-13
 */
public interface ActivityTemplateService {

    void saveTemplate(ActivityTemplate activityTemplate);

    void deleteActivityTemplate(Long code, Long headId, String stage,String type);

    ActivityTemplate getTemplate(Long tmpCode);

    String updateSmsTemplate(ActivityTemplate activityTemplate,String flag);

    /**
     * 获取活动文案的预览
     * @param code
     * @return
     */
    String getActivityTemplateContent(Long code,String scene);

    List<ActivityTemplate> getSmsTemplateList(Long headId,String isPersonal,String scene);

    boolean checkTemplateUsed(Long templateCode,Long headId,String stage,String type);

    void removeSmsSelected(String type, Long headId, String stage, Long groupId);

    void setSmsCode(Long groupId, Long tmpCode, Long headId, String type, String stage);

}
