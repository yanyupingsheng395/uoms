package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyGroupTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface QywxDailyConfigMapper {

    /**
     * 获取用户组列表
     * @param activeList
     * @return
     */
    List<DailyGroupTemplate> getUserGroupList();

    /**
     * 更新check_flag 为 'Y'
     * @return
     */
    int updateCheckFlagY();

    /**
     * 更新群组表的验证信息和备注信息
     * @param whereInfo
     * @param remark
     * @return
     */
    int updateCheckFlagAndRemark(@Param("whereInfo") String whereInfo, @Param("remark") String remark);

    /**
     * 验证补贴未配置的情况
     * @return
     */
    void updateCheckFlagNotConfigCoupon(@Param("remark") String remark);

    int validCheckedUserGroup(@Param("activeList") List<String> activeList);


    void updateWxMsgId(String lifeCycle, String pathActive, Long qywxId);

    /**
     * 获取组上配置的文案信息
     * @param userValue
     * @param lifeCycle
     * @param pathActive
     * @param tarType
     * @return
     */
    Map<String, Object> findMsgInfo(String userValue, String lifeCycle, String pathActive, String tarType);

    /**
     * 为组设置文案信息
     * @param groupId
     * @param smsCode
     */
    void setSmsCode(Long groupId, String smsCode);

    /**
     * 恢复文案的修改标记
     */
    void resetOpFlag();
}
