package com.linksteady.operate.service;

import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.domain.SpuCycle;

import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
public interface SmsTemplateService {

    List<SmsTemplate> getSmsTemplateList(int startRow, int endRow, SmsTemplate smsTemplate);

    int getTotalCount(SmsTemplate smsTemplate);

    void saveSmsTemplate(SmsTemplate smsTemplate);

    int refrenceCount(String smsCode);

    void deleteSmsTemplate(String smsCode);

    SmsTemplate getSmsTemplate(String smsCode);

    SmsTemplate getSmsTemplateBySmsCode(String smsCode);

    void update(SmsTemplate smsTemplate);

    List<SmsTemplate> getTemplateByGroupId(String groupId);

    List<SmsTemplate> getTemplate(String userValue, String pathActive, String lifeCycle);
}
