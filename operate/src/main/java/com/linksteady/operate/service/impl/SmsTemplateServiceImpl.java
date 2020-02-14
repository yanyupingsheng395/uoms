package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.config.ConfigCacheManager;
import com.linksteady.operate.dao.SmsTemplateMapper;
import com.linksteady.operate.dao.SpuCycleMapper;
import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.domain.SpuCycle;
import com.linksteady.operate.service.SmsTemplateService;
import com.linksteady.operate.service.SpuCycleService;
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
        if (StringUtils.isNotEmpty(isCouponUrl) && isCouponUrl.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(couponUrl, configCacheManager.getConfigMap().get("op.daily.sms.cunponurl"));
        }
        if (StringUtils.isNotEmpty(isCouponName) && isCouponName.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(couponName, configCacheManager.getConfigMap().get("op.daily.sms.couponname"));
        }
        if (StringUtils.isNotEmpty(isProductUrl) && isProductUrl.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(prodUrl, configCacheManager.getConfigMap().get("op.daily.sms.produrl"));
        }
        if (StringUtils.isNotEmpty(isProductName) && isProductName.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(prodName, configCacheManager.getConfigMap().get("op.daily.sms.prodname"));
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
    public List<SmsTemplate> getTemplateByGroupId(String groupId) {
        List<String> groupIdList = Arrays.asList(groupId.split(","));
        return smsTemplateapper.getTemplateByGroupId(groupIdList);
    }

    @Override
    public List<SmsTemplate> getTemplate(String userValue, String pathActive, String lifeCycle) {
        List<SmsTemplate> template = smsTemplateapper.getTemplate(userValue, pathActive, lifeCycle);
        List<Map<String,Object>> groupData = smsTemplateapper.getGroupValue();
        template.stream().forEach(x->{
            final StringBuffer userValueTmp = new StringBuffer(x.getUserValue());
            final StringBuffer pathActiveTmp = new StringBuffer(x.getPathActive());
            final StringBuffer lifeCycleTmp = new StringBuffer(x.getLifeCycle());
            String smsCode = x.getSmsCode();
            groupData.stream().filter(v->v.get("SMS_CODE").equals(smsCode)).forEach(v1->{
                userValueTmp.append(",");
                userValueTmp.append(v1.get("USER_VALUE"));
                pathActiveTmp.append(",");
                pathActiveTmp.append(v1.get("PATH_ACTIVE"));
                lifeCycleTmp.append(",");
                lifeCycleTmp.append(v1.get("LIFECYLE"));
            });
            if(userValueTmp.length() > 0) {
                x.setUserValue(Arrays.stream(userValueTmp.toString().split(",")).distinct().sorted(StringUtils::compare).collect(Collectors.joining(",")));
            }
            if(pathActiveTmp.length() > 0) {
                x.setPathActive(Arrays.stream(pathActiveTmp.toString().split(",")).distinct().sorted(StringUtils::compare).collect(Collectors.joining(",")));
            }
            if(lifeCycleTmp.length() > 0) {
                x.setLifeCycle(Arrays.stream(lifeCycleTmp.toString().split(",")).distinct().sorted(StringUtils::compare).collect(Collectors.joining(",")));
            }
        });
        List<SmsTemplate> collect = template.stream().filter(x -> (StringUtils.isNotEmpty(x.getUserValue()) && x.getUserValue().contains(userValue))
                && (StringUtils.isNotEmpty(x.getLifeCycle()) && x.getLifeCycle().contains(lifeCycle))
                && (StringUtils.isNotEmpty(x.getPathActive()) && x.getPathActive().contains(pathActive))
        ).collect(Collectors.toList());
        return collect;
    }

    public static void main(String[] args) {
        List<String> l = Lists.newArrayList();
        l.add("ULC_01");
        l.add("ULC_03");
        l.add("ULC_02");
        l.add("ULC_02");
        List<String> collect = l.stream().distinct().sorted(StringUtils::compare).collect(Collectors.toList());
        String join = String.join(",",collect);
        System.out.println(join);
    }

    @Override
    public List<String> getSmsUsedGroupInfo(String smsCode) {
        ConfigCacheManager configCacheManager = ConfigCacheManager.getInstance();
        configCacheManager.getLifeCycleMap().get("");
        List<String> data = smsTemplateapper.getSmsUsedGroupInfo(smsCode);
        List<String> result = data.stream().map(x -> {
            String[] tmpArray = x.split(",");
            StringBuilder tmp = new StringBuilder();
            tmp.append(configCacheManager.getUserValueMap().get(tmpArray[0]));
            tmp.append(",");
            tmp.append(configCacheManager.getLifeCycleMap().get(tmpArray[1]));
            tmp.append(",");
            tmp.append(configCacheManager.getPathActiveMap().get(tmpArray[2]));
            return tmp.toString();
        }).collect(Collectors.toList());
        return result;
    }
}
