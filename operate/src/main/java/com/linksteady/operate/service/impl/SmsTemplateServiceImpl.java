package com.linksteady.operate.service.impl;

import com.linksteady.operate.config.ConfigCacheManager;
import com.linksteady.operate.dao.SmsTemplateMapper;
import com.linksteady.operate.dao.SpuCycleMapper;
import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.domain.SpuCycle;
import com.linksteady.operate.service.SmsTemplateService;
import com.linksteady.operate.service.SpuCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
@Service
public class SmsTemplateServiceImpl implements SmsTemplateService {

    @Autowired
    private SmsTemplateMapper smsTemplateapper;

    @Override
    public List<SmsTemplate> getSmsTemplateList(int startRow, int endRow,SmsTemplate smsTemplate) {
        return smsTemplateapper.getSmsTemplateList(startRow, endRow, smsTemplate);
    }

    @Override
    public int getTotalCount(SmsTemplate smsTemplate) {
        return smsTemplateapper.getTotalCount(smsTemplate);
    }

    @Override
    public void saveSmsTemplate(SmsTemplate smsTemplate) {
        smsTemplateapper.saveSmsTemplate(smsTemplate);
    }

    @Override
    public int refrenceCount(String smsCode) {
        return smsTemplateapper.refrenceCount(smsCode);
    }

    @Override
    public void deleteSmsTemplate(String smsCode) {
        smsTemplateapper.deleteSmsTemplate(smsCode);
    }

    @Override
    public SmsTemplate getSmsTemplate(String smsCode) {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        SmsTemplate smsTemplate = smsTemplateapper.getSmsTemplate(smsCode);
        String isCouponUrl = smsTemplate.getIsCouponUrl();
        String isCouponName = smsTemplate.getIsCouponName();
        String isProductUrl = smsTemplate.getIsProductUrl();
        String isProductName = smsTemplate.getIsProductName();
        String couponUrl = "${COUPON_URL}";
        String couponName = "${COUPON_NAME}";
        String prodName = "${PROD_NAME}";
        String prodUrl = "${PROD_URL}";
        String smsContent = smsTemplate.getSmsContent();
        if (isCouponUrl.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(couponUrl, configCacheManager.getConfigMap().get("op.daily.sms.cunponurl"));
        }
        if (isCouponName.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(couponName, configCacheManager.getConfigMap().get("op.daily.sms.couponname"));
        }
        if (isProductUrl.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(prodName, configCacheManager.getConfigMap().get("op.daily.sms.produrl"));
        }
        if (isProductName.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(prodUrl, configCacheManager.getConfigMap().get("op.daily.sms.prodname"));
        }
        smsTemplate.setSmsContent(smsContent);
        return  smsTemplate;
    }

    @Override
    public void update(SmsTemplate smsTemplate) {
        smsTemplateapper.update(smsTemplate);
    }

    @Override
    public List<SmsTemplate> getTemplateByGroupId(String groupId) {
        List<String> groupIdList = Arrays.asList(groupId.split(","));
        return smsTemplateapper.getTemplateByGroupId(groupIdList);
    }
}
