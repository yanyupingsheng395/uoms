package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.ActivityTemplateMapper;
import com.linksteady.operate.domain.ActivityTemplate;
import com.linksteady.operate.service.ActivityTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-13
 */
@Service
public class ActivityTemplateServiceImpl implements ActivityTemplateService {

    @Autowired
    private ActivityTemplateMapper activityTemplateMapper;

    @Override
    public List<ActivityTemplate> getTemplateList(int startRow, int endRow, String code) {
        return activityTemplateMapper.getTemplateList(startRow, endRow, code);
    }

    @Override
    public int getTotalCount(String code) {
        return activityTemplateMapper.getTotalCount(code);
    }

    @Override
    public void saveTemplate(ActivityTemplate activityTemplate) {
        activityTemplateMapper.saveTemplate(activityTemplate);
    }

    @Override
    public int refrenceCount(String code) {
        return 0;
    }

    @Override
    public void deleteTemplate(String code) {
        activityTemplateMapper.deleteTemplate(code);
    }

    @Override
    public ActivityTemplate getTemplate(String code) {
        return activityTemplateMapper.getTemplate(code);
    }

    @Override
    public void update(ActivityTemplate activityTemplate) {
        activityTemplateMapper.update(activityTemplate);
    }
}
