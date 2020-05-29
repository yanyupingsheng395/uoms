package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.SmsTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SmsTemplateMapper extends MyMapper<SmsTemplate> {

    List<SmsTemplate> getSmsTemplateList(@Param("limit") int limit, @Param("offset") int offset);

    int getTotalCount();

    void saveSmsTemplate(SmsTemplate smsTemplate);

    int refrenceCount(@Param("smsCode") String smsCode);

    void deleteSmsTemplate(@Param("smsCode") String smsCode);

    SmsTemplate getSmsTemplate(@Param("smsCode") String smsCode);

    void update(@Param("smsTemplate") SmsTemplate smsTemplate);

    List<String> getSmsUsedGroupInfo(String smsCode);
}