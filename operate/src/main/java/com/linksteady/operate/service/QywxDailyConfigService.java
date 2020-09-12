package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyGroupTemplate;
import com.linksteady.operate.exception.OptimisticLockException;

import java.util.List;
import java.util.Map;

public interface QywxDailyConfigService {

    /**
     * 获取群组列表
     * @return
     */
    List<DailyGroupTemplate> getUserGroupList();

    /**
     * 对用户群组信息进行校验
     * @return
     */
    boolean validUserGroup();


    /**
     * 针对企业微信的成长组校验
     * @return
     */
    boolean validUserGroupForQywx();

    void updateWxMsgId(String lifeCycle, String pathActive, Long qywxId);

    /**
     * 获取当前群组上配置的短信、微信消息
     * @param userValue
     * @param lifeCycle
     * @param pathActive
     * @param tarType
     * @return
     */
    Map<String, Object> getConfigInfoByGroup(String userValue, String lifeCycle, String pathActive, String tarType);

    /**
     * 将文案按给定的规则配置到用户群组上去
     * @return
     */
    void autoSetGroupCoupon() throws OptimisticLockException;

    /**
     * 获取理解用户数据
     * @return
     */
    List<Map<String, String>> getGroupDescription();

    /**
     * 设置群组信息
     * @param groupId
     * @param smsCode
     */
    void setSmsCode(Long groupId, String smsCode);

    /**
     * 恢复文案的配置标记
     */
    void resetOpFlag();
}
