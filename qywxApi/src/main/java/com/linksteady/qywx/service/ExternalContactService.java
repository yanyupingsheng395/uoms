package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.ExternalContact;
import com.linksteady.qywx.exception.WxErrorException;

import java.util.List;
import java.util.Map;

public interface ExternalContactService {

    /**
     * 同步外部联系人
     */
    void syncExternalContact() throws Exception;

    /**
     * 获取某个用户下的客户列表
     * @param userId
     * @return
     */
    List<String> findExternalContractList(String userId) throws WxErrorException;

    /**
     * 获取每个客户的详细信息
     */
    ExternalContact getExternalContractDetail(String followerUserId,String externalUserid) throws WxErrorException;


    Map<String,Object> getExternalContractDetailBatch(String cursor, String followerUserId, List<String> externalUserList) throws WxErrorException;

    /**
     * 保存外部联系人ID
     */
    void saveExternalUserId(String followerUserId,String externalUserId);

    /**
     * 更新外部联系人
     */
    void updateExternalContract(ExternalContact externalContact);

    void deleteExternalContract(String followerUserId,String externalUserId);

    int selectLocalContactCount();

    /**
     * 分页获取当前企业的外部联系人
     */
    List<ExternalContact> selectLocalContractList(int limit,int offset);

    /**
     * 分页获取导购关系引导
     */
    List<ExternalContact> getGuidanceList(int limit,
                                          int offset,
                                          String followUserId,
                                          String relation,
                                          String loss,
                                          String stageValue,
                                          String interval);

    /**
     * 分页获取导购关系引导
     */
    List<ExternalContact> getQywxGuidanceList(String followUserId,String relation,String loss,String stagevalue,String interval, Integer offset, Integer limit);

    int getGuidanceCount(String followUserId,
                         String relation,
                         String loss,
                         String stagevalue,
                         String interval);

    /**
     *分页获取导购关系引导
     * 类型：未购买。根据添加时间筛选人员
     */
    List<ExternalContact> getAddTimeList( String followUserId, String addtime,Integer offset, Integer limit);

    int getQywxGuidanceCount(String followUserId, String relation, String loss, String stagevalue, String interval);

    int getgetAddTimeCount(String followUserId);
}
