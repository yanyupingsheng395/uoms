package com.linksteady.operate.service.impl;

import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.SmsTemplateMapper;
import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.service.SmsTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hxcao on 2019-04-29
 */
@Service
public class SmsTemplateServiceImpl implements SmsTemplateService {

    @Autowired
    private SmsTemplateMapper smsTemplateapper;

    @Autowired
    private ConfigService configService;

    @Override
    public List<SmsTemplate> getSmsTemplateList(int limit, int offset) {
        return smsTemplateapper.getSmsTemplateList(limit, offset);
    }

    @Override
    public int getTotalCount() {
        return smsTemplateapper.getTotalCount();
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
        if (StringUtils.isNotEmpty(isCouponUrl) && isCouponUrl.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(couponUrl, configService.getValueByName("op.daily.sms.cunponurl"));
        }
        if (StringUtils.isNotEmpty(isCouponName) && isCouponName.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(couponName, configService.getValueByName("op.daily.sms.couponname"));
        }
        if (StringUtils.isNotEmpty(isProductUrl) && isProductUrl.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(prodUrl, configService.getValueByName("op.daily.sms.produrl"));
        }
        if (StringUtils.isNotEmpty(isProductName) && isProductName.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(prodName, configService.getValueByName("op.daily.sms.prodname"));
        }
        smsTemplate.setSmsContent(smsContent);
        return  smsTemplate;
    }

    @Override
    public SmsTemplate getSmsTemplateBySmsCode(String smsCode) {
        return  smsTemplateapper.getSmsTemplate(smsCode);
    }

    @Override
    public void update(SmsTemplate smsTemplate) {
        smsTemplateapper.update(smsTemplate);
    }

    @Override
    public List<String> getSmsUsedGroupInfo(String smsCode) {
        Map<String, String> pathActiveMap =configService.selectDictByTypeCode("PATH_ACTIVE");
        Map<String, String> userValueMap =configService.selectDictByTypeCode("USER_VALUE");
        Map<String, String> lifeCycleMap =configService.selectDictByTypeCode("LIFECYCLE");
        List<String> data = smsTemplateapper.getSmsUsedGroupInfo(smsCode);
        List<String> result = data.stream().map(x -> {
            String[] tmpArray = x.split(",");
            StringBuilder tmp = new StringBuilder();
            tmp.append(userValueMap.get(tmpArray[0]));
            tmp.append(",");
            tmp.append(lifeCycleMap.get(tmpArray[1]));
            tmp.append(",");
            tmp.append(pathActiveMap.get(tmpArray[2]));
            return tmp.toString();
        }).collect(Collectors.toList());
        return result;
    }
}