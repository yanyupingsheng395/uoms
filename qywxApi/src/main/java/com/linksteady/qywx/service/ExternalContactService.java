package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.ExternalContact;
import com.linksteady.qywx.domain.MappingStatis;
import com.linksteady.qywx.domain.PhoneFixStatis;
import com.linksteady.qywx.domain.RepeatStatis;
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
     * 手机号不正确列表的数量
     */
    int selectRemarkInvalidCount(String corpId, String followUserId);

    /**
     * 分页获取手机号维护不正确的列表 (空或者格式不正确)
     */
    List<ExternalContact> selectRemarkInvalid(int offset,int limit,String corpId, String followUserId);


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
    List<ExternalContact> getQywxGuidanceList(String corpId,String followUserId,String relation,String loss,String stagevalue,String interval);

    int getGuidanceCount(String followUserId,
                         String relation,
                         String loss,
                         String stagevalue,
                         String interval);

    /**
     *分页获取导购关系引导
     * 类型：未购买。根据添加时间筛选人员
     */
    List<ExternalContact> getAddTimeList(String corpId, String followUserId, String addtime);

    /**
     * 手机号维护情况的统计
     */
    List<PhoneFixStatis> getPhoneFixStatis(String corpId);

    /**
     * 重复添加情况统计
     */
    List<RepeatStatis> getRepeatStatis(String corpId);

    /**
     * 匹配情况
     */
    MappingStatis getMappingStatis(String corpId);

}
