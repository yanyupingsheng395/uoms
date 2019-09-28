package com.linksteady.operate.service;

import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.domain.SpuCycle;

import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
public interface SmsTemplateService {

    List<SmsTemplate> getSmsTemplateList(int startRow, int endRow,String smsCode, String groupId);

    int getTotalCount(String smsCode, String groupId);

    void saveSmsTemplate(String smsCode,String smsContent, String isCoupon);

    int refrenceCount(String smsCode);

    void deleteSmsTemplate(String smsCode);

    SmsTemplate getSmsTemplate(String smsCode);
}
