package com.linksteady.operate.dao;

import com.linksteady.operate.config.MyMapper;
import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.domain.SpuCycle;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface SmsTemplateMapper extends MyMapper<SmsTemplate> {

    List<SmsTemplate> getSmsTemplateList(@Param("startRow") int startRow, @Param("endRow") int endRow);

    int getTotalCount();

    void saveSmsTemplate(SmsTemplate smsTemplate);

    int refrenceCount(@Param("smsCode") String smsCode);

    void deleteSmsTemplate(@Param("smsCode") String smsCode);

    SmsTemplate getSmsTemplate(@Param("smsCode") String smsCode);

    void update(@Param("smsTemplate") SmsTemplate smsTemplate);
}