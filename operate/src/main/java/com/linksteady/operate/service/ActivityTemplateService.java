package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityTemplate;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-13
 */
public interface ActivityTemplateService {

    void saveTemplate(ActivityTemplate activityTemplate);

    int refrenceCount(String smsCode);

    void deleteTemplate(String smsCode);

    ActivityTemplate getTemplate(String tmpCode);

    void update(ActivityTemplate activityTemplate);

    /**
     * 获取活动文案的预览
     * @param code
     * @return
     */
    String getActivityTemplateContent(String code,String scene);

    List<ActivityTemplate> getSmsTemplateList(ActivityTemplate activityTemplate);
}
