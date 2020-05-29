package com.linksteady.operate.service;

import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.domain.SpuCycle;

import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
public interface SmsTemplateService {

    List<SmsTemplate> getSmsTemplateList(int limit, int offset);

    int getTotalCount();

    void saveSmsTemplate(SmsTemplate smsTemplate);

    int refrenceCount(String smsCode);

    void deleteSmsTemplate(String smsCode);

    SmsTemplate getSmsTemplate(String smsCode);

    SmsTemplate getSmsTemplateBySmsCode(String smsCode);

    void update(SmsTemplate smsTemplate);

    List<String> getSmsUsedGroupInfo(String smsCode);
}
