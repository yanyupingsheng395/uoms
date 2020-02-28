package com.linksteady.operate.service;

import com.linksteady.operate.domain.ActivityTemplate;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-13
 */
public interface ActivityTemplateService {

    int getTotalCount(String code);

    void saveTemplate(ActivityTemplate activityTemplate);

    int refrenceCount(String smsCode);

    void deleteTemplate(String smsCode);

    ActivityTemplate getTemplate(String tmpCode);

    void update(ActivityTemplate activityTemplate);

    List<ActivityTemplate> getTemplateList(int start, int end, String code);

    int checkCode(String code);

    ActivityTemplate getReplacedTmp(String code);

    List<ActivityTemplate> getSmsTemplateList(ActivityTemplate activityTemplate);
}
