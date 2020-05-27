package com.linksteady.operate.service;

import com.linksteady.common.domain.ResponseBo;

import java.util.Map;

public interface DailyConfigService {

    Map<String, Object> usergroupdesc(String userValue, String pathActive, String lifecycle);

    boolean validUserGroup();

    void deleteSmsGroup(String groupId);

    /**
     * 针对企业微信的成长组校验
     * @return
     */
    boolean validUserGroupForQywx();

    void updateWxMsgId(String groupId, String qywxId);

    Map<String, Object> getCurrentGroupData(String userValue, String lifeCycle, String pathActive, String tarType);

    ResponseBo resetGroupCoupon();
}
