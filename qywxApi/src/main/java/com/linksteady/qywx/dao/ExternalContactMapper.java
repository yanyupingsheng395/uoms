package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.ExternalContact;
import com.linksteady.qywx.domain.MappingStatis;
import com.linksteady.qywx.domain.PhoneFixStatis;
import com.linksteady.qywx.domain.RepeatStatis;

import java.util.List;

public interface ExternalContactMapper {

    /**
     * 保存外部联系人
     * @param externalUserList 外部联系人姓名列表
     */
    void saveExternalUserList(String followerUserId,List<String> externalUserList);

    /**
     * 更新外部联系人
     * @param externalContact
     */
    void updateExternalContract(ExternalContact externalContact);

    /**
     * 删除外部联系人
     */
    void deleteExternalContract(String followerUserId,String externalUserId);

    /**
     * 保存外部联系人
     */
    void saveExternalContract(ExternalContact externalContact);

    /**
     * 分页 获取外部联系人的总条数
     * @return
     */
    int selectLocalContactCount();

    /**
     * 分页获取外部联系人
     */
    List<ExternalContact> selectLocalContractList(int limit, int offset);

    /**
     * 分页 获取 导购引导的条数
     * @return
     */
    int getGuidanceCount(String whereInfo);

    /**
     * 分页获取外部联系人
     */
    List<ExternalContact> getGuidanceList(String whereInfo,int limit, int offset);

    void updateDeleteFlag();

    void deleteExternalUser();

    void saveExternalContractBatch(List<ExternalContact> externalContacts);

    void saveExternalUserId(String followerUserId, String externalUserId);

    /**
     * 分页 获取手机号不正确外部联系人的总条数
     * @param corpId
     * @return
     */
    int selectRemarkInvalidCount(String corpId,String followerUserId);

    /**
     * 分页获取手机号不正确外部联系人
     */
    List<ExternalContact> selectRemarkInvalid(int offset, int limit,String corpId, String followerUserId);

    List<PhoneFixStatis> getPhoneFixStatis(String corpId);

    List<RepeatStatis> getRepeatStatis(String corpId);

    MappingStatis getMappingStatis(String corpId);

}