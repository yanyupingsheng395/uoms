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

//    /**
//     * 保存外部联系人列表
//     */
//    void saveExternalUserList(String corpId,String followerUserId,List<String> externalUserList);

//    /**
//     * 保存外部联系人ID
//     */
//    void saveExternalUserId(String corpId,String followerUserId,String externalUserId);

//    /**
//     * 更新外部联系人
//     */
//    void updateExternalContract(ExternalContact externalContact);

//    /**
//     * 获取本地的所有客户列表
//     */
//    List<ExternalContact> selectLocalContractList(String corpId);

//    /**
//     * 获取本地当前用户的所有客户列表
//     */
//    List<ExternalContact> selectLocalContractListByUserId(String userId);
//
//    int selectLocalContracCountByUserId(String userId);

//    /**
//     * 获取当前外部用户的详细信息
//     * @param corpId
//     * @param followUserId
//     * @param externalUserId
//     * @return
//     */
//    ExternalContact getUserInfo(String corpId, String followUserId, String externalUserId);


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

    int getGuidanceCount(String followUserId,
                         String relation,
                         String loss,
                         String stagevalue,
                         String interval);



}
