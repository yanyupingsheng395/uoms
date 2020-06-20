package com.linksteady.operate.service.impl;

import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.config.PushConfig;
import com.linksteady.operate.dao.SmsTemplateMapper;
import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.service.SmsTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hxcao on 2019-04-29
 */
@Service
@Slf4j
public class SmsTemplateServiceImpl implements SmsTemplateService {

    @Autowired
    private SmsTemplateMapper smsTemplateMapper;

    @Autowired
    private ConfigService configService;

    @Autowired
    private PushConfig pushConfig;

    @Override
    public List<SmsTemplate> selectSmsTemplateListWithGroup(int limit, int offset, long groupId) {
        return smsTemplateMapper.selectSmsTemplateListWithGroup(limit, offset, groupId);
    }

    @Override
    public List<SmsTemplate> selectSmsTemplateList(int limit, int offset) {
        return smsTemplateMapper.selectSmsTemplateList(limit, offset);
    }


    @Override
    public int getTotalCount() {
        return smsTemplateMapper.getTotalCount();
    }

    @Override
    public void saveSmsTemplate(SmsTemplate smsTemplate) {
        smsTemplateMapper.saveSmsTemplate(smsTemplate);
    }

    @Override
    public int refrenceCount(String smsCode) {
        return smsTemplateMapper.refrenceCount(smsCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSmsTemplate(String smsCode) {
        if(smsTemplateMapper.refrenceCount(smsCode)> 0)
        {
            //删除券的引用关系
            smsTemplateMapper.updateSmsCodeNull(smsCode);
        }
        smsTemplateMapper.deleteSmsTemplate(smsCode);
    }

    /**
     * 获取文案的内容
     * @param smsCode
     * @param  scene DISPLAY为了预览显示 一律加上签名和退订方式  SEND为了实际发送 需要根据配置决定是否加签名和退订方式
     * @return
     */
    @Override
    public String getSmsContent(String smsCode,String scene) {
        SmsTemplate smsTemplate = smsTemplateMapper.getSmsTemplate(smsCode);
        String isCouponUrl = smsTemplate.getIsCouponUrl();
        String isCouponName = smsTemplate.getIsCouponName();
        String isProductUrl = smsTemplate.getIsProductUrl();
        String isProductName = smsTemplate.getIsProductName();
        String couponUrl = "${补贴短链}";
        String couponName = "${补贴名称}";
        String prodName = "${商品名称}";
        String prodUrl = "${商品详情页短链}";

        String smsContent =smsTemplate.getSmsContent();
        if (StringUtils.isNotEmpty(isCouponUrl) && isCouponUrl.equalsIgnoreCase("1")) {
            //短链替换的时候前后各补一个空格
            smsContent = smsContent.replace(couponUrl, " "+configService.getValueByName("op.sms.url")+" ");
        }
        if (StringUtils.isNotEmpty(isCouponName) && isCouponName.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(couponName, configService.getValueByName("op.sms.couponname"));
        }
        if (StringUtils.isNotEmpty(isProductUrl) && isProductUrl.equalsIgnoreCase("1")) {
            //短链替换的时候前后各补一个空格
            smsContent = smsContent.replace(prodUrl, " "+configService.getValueByName("op.sms.url")+" ");
        }
        if (StringUtils.isNotEmpty(isProductName) && isProductName.equalsIgnoreCase("1")) {
            smsContent = smsContent.replace(prodName, configService.getValueByName("op.sms.prodname"));
        }

        //获取签名
        String signature=pushConfig.getSignature();
        String signatureFlag=pushConfig.getSignatureFlag();

        String unsubscribe=pushConfig.getUnsubscribe();
        String unsubscribeFlag=pushConfig.getUnsubscribeFlag();

        if("DISPLAY".equals(scene))
        {
            smsContent =signature+smsContent;
            smsContent=smsContent+unsubscribe;
        }else if("SEND".equals(scene))
        {
            //需要系统自动加上签名
            if(null!=signatureFlag&&"Y".equals(signatureFlag))
            {
                smsContent =signature+smsContent;
            }

            //需要加上退订方式
            if(null!=unsubscribeFlag&&"Y".equals(unsubscribeFlag))
            {
                smsContent=smsContent+unsubscribe;
            }
        }

        return  smsContent;
    }

    /**
     * 读取文案
     * @param smsCode
     * @return
     */
    @Override
    public SmsTemplate getSmsTemplateBySmsCode(String smsCode) {
        return  smsTemplateMapper.getSmsTemplate(smsCode);
    }

    @Override
    public void update(SmsTemplate smsTemplate) {
        smsTemplateMapper.update(smsTemplate);
    }

    @Override
    public List<String> getSmsUsedGroupInfo(String smsCode) {
        Map<String, String> pathActiveMap =configService.selectDictByTypeCode("PATH_ACTIVE");
        Map<String, String> userValueMap =configService.selectDictByTypeCode("USER_VALUE");
        Map<String, String> lifeCycleMap =configService.selectDictByTypeCode("LIFECYCLE");
        List<String> data = smsTemplateMapper.getSmsUsedGroupInfo(smsCode);
        List<String> result = data.stream().map(x -> {
            String[] tmpArray = x.split(",");
            StringBuilder tmp = new StringBuilder();
            tmp.append(userValueMap.get(tmpArray[0]));
            tmp.append(" , ");
            tmp.append(lifeCycleMap.get(tmpArray[1]));
            tmp.append(" , ");
            tmp.append(pathActiveMap.get(tmpArray[2]));
            return tmp.toString();
        }).collect(Collectors.toList());
        return result;
    }
}