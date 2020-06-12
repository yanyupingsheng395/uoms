package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.SmsTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SmsTemplateMapper extends MyMapper<SmsTemplate> {

    List<SmsTemplate> selectSmsTemplateListWithGroup(@Param("limit") int limit, @Param("offset") int offset, @Param("groupId") long groupId);

    List<SmsTemplate> selectSmsTemplateList(int limit, int offset);

    int getTotalCount();

    void saveSmsTemplate(SmsTemplate smsTemplate);

    int refrenceCount(@Param("smsCode") String smsCode);

    void deleteSmsTemplate(@Param("smsCode") String smsCode);

    SmsTemplate getSmsTemplate(@Param("smsCode") String smsCode);

    void update(@Param("smsTemplate") SmsTemplate smsTemplate);

    List<String> getSmsUsedGroupInfo(String smsCode);

    /**
     * 解除组和优惠券的绑定关系
     * @param smsCode
     */
    void updateSmsCodeNull(String smsCode);


}