package com.linksteady.operate.service.impl;

import com.linksteady.operate.config.ConfigCacheManager;
import com.linksteady.operate.dao.ActivityTemplateMapper;
import com.linksteady.operate.domain.ActivityTemplate;
import com.linksteady.operate.service.ActivityTemplateService;
import org.apache.commons.lang3.StringUtils;
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
    public int checkCode(String code) {
        return activityTemplateMapper.checkCode(code);
    }

    @Override
    public ActivityTemplate getReplacedTmp(String code) {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        ActivityTemplate activityTemplate = activityTemplateMapper.getTemplate(code);
        String isProdUrl = activityTemplate.getIsProdUrl();
        String isProdName = activityTemplate.getIsProdName();
        String isPrice = activityTemplate.getIsPrice();
        String prodUrl = "${PROD_URL}";
        String prodName = "${PROD_URL}";
        String price = "${PRICE}";
        String content = activityTemplate.getContent();
        if (StringUtils.isNotEmpty(isProdUrl) && isProdUrl.equalsIgnoreCase("1")) {
            content = content.replace(prodUrl, configCacheManager.getConfigMap().get("op.activity.sms.prodUrl"));
        }
        if (StringUtils.isNotEmpty(isProdName) && isProdName.equalsIgnoreCase("1")) {
            content = content.replace(prodName, configCacheManager.getConfigMap().get("op.activity.sms.prodName"));
        }
        if (StringUtils.isNotEmpty(isPrice) && isPrice.equalsIgnoreCase("1")) {
            content = content.replace(price, configCacheManager.getConfigMap().get("op.activity.sms.price"));
        }
        activityTemplate.setContent(content);
        return activityTemplate;
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
