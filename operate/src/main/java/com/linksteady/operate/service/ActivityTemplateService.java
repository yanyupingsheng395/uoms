package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityTemplate;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-13
 */
public interface ActivityTemplateService {

    void saveTemplate(ActivityTemplate activityTemplate,String currentUser);

    void deleteActivityTemplate(Long code, Long headId, String stage,String type);

    ActivityTemplate getTemplate(Long tmpCode);

    String updateSmsTemplate(ActivityTemplate activityTemplate,String flag,String currentUser);

    /**
     * 获取活动文案的预览
     * @param code
     * @return
     */
    String getActivityTemplateContent(Long code,String scene);

    List<ActivityTemplate> getSmsTemplateList(Long headId,String isPersonal,String scene,String stage,String type);

    boolean checkTemplateUsed(Long templateCode,Long headId,String stage,String type);

    void removeSmsSelected(String type, Long headId, String stage, Long groupId,String currentUserName);

    void setSmsCode(Long groupId, Long tmpCode, Long headId,String stage, String type,String currentUserName);

}
