package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.ExternalContact;

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


    List<ExternalContact> selectExternalUserList(String followUserId,String addtime,Integer offset, Integer limit);

    /**
     * 获取导购关系指引列表
     * @param followUserId
     * @param relation
     * @param loss
     * @param stagevalue
     * @param interval
     * @param offset
     * @param limit
     * @return
     */
    List<ExternalContact> getQywxGuidanceList(String followUserId, String relation, String loss, String stagevalue, String interval,Integer offset, Integer limit);

    /**
     * 获取导购关系指引记录数
     * @param followUserId
     * @param relation
     * @param loss
     * @param stagevalue
     * @param interval
     * @return
     */
    int getQywxGuidanceCount(String followUserId, String relation, String loss, String stagevalue, String interval);

    int getgetAddTimeCount(String followUserId);

    ExternalContact selectExternalUser(String followUserId,String externalUserId);

}