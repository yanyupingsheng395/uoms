package com.linksteady.operate.service;

import com.linksteady.operate.domain.SmsTemplate;

import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
public interface SmsTemplateService {

    List<SmsTemplate> selectSmsTemplateListWithGroup(int limit, int offset, long groupId);

    List<SmsTemplate> selectSmsTemplateList(int limit, int offset);

    int getTotalCount();

    void saveSmsTemplate(SmsTemplate smsTemplate);

    int refrenceCount(String smsCode);

    void deleteSmsTemplate(String smsCode);

    /**
     * 获取文案的内容
     * @param smsCode
     * @param scene DISPLAY 显示 SEND 发送
     * @return
     */
    String getSmsContent(String smsCode,String scene);



    SmsTemplate getSmsTemplateBySmsCode(String smsCode);

    void update(SmsTemplate smsTemplate);

    List<String> getSmsUsedGroupInfo(String smsCode);
}
