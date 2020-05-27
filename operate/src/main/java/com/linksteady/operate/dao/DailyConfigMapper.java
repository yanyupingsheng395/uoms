package com.linksteady.operate.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DailyConfigMapper {

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

    int validCheckedUserGroup(@Param("activeList") List<String> activeList);

    void deleteSmsGroup(List<String> groupIds);

    void updateWxMsgId(String groupId, String qywxId);

    Map<String, Object> findMsgInfo(String userValue, String lifeCycle, String pathActive, String tarType);
}
