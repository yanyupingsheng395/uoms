package com.linksteady.operate.dao;

import com.linksteady.operate.config.MyMapper;
import com.linksteady.operate.domain.SmsTemplate;
import com.linksteady.operate.domain.SpuCycle;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SmsTemplateMapper extends MyMapper<SmsTemplate> {

    List<SmsTemplate> getSmsTemplateList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("smsTemplate") SmsTemplate smsTemplate);

    int getTotalCount(@Param("smsTemplate") SmsTemplate smsTemplate);

    void saveSmsTemplate(SmsTemplate smsTemplate);

    int refrenceCount(@Param("smsCode") String smsCode);

    void deleteSmsTemplate(@Param("smsCode") String smsCode);

    SmsTemplate getSmsTemplate(@Param("smsCode") String smsCode);

    void update(@Param("smsTemplate") SmsTemplate smsTemplate);

    List<SmsTemplate> getTemplateByGroupId(@Param("groupIdList") List<String> groupIdList);

    List<SmsTemplate> getTemplate(String userValue, String pathActive, String lifeCycle);

    List<String> getSmsUsedGroupInfo(String smsCode);

    List<Map<String,Object>> getGroupValue();
}