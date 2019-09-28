package com.linksteady.operate.dao;

import com.linksteady.operate.config.MyMapper;
import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.domain.SpuCycle;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface SmsTemplateMapper extends MyMapper<SmsTemplate> {

    List<SmsTemplate> getSmsTemplateList(@Param("startRow") int startRow, @Param("endRow") int endRow,
                                         @Param("smsCode") String smsCode, @Param("groupId") String groupId);

    int getTotalCount(@Param("smsCode") String smsCode, @Param("groupId") String groupId);

    void saveSmsTemplate(@Param("smsCode") String smsCode, @Param("smsContent") String smsContent, @Param("isCoupon") String isCoupon);

    int refrenceCount(@Param("smsCode") String smsCode);

    void deleteSmsTemplate(@Param("smsCode") String smsCode);

    SmsTemplate getSmsTemplate(@Param("smsCode") String smsCode);
}