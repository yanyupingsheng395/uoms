package com.linksteady.qywx.service;

import com.linksteady.common.service.IService;
import com.linksteady.qywx.domain.*;

import java.util.List;

public interface UserService extends IService<QywxUser> {

    QywxUser findUser(String userId, String corpId);

    void saveUser(UserInfoThirdParty userInfoThirdParty, UserDetailThirdParty userDetailThirdParty);

    void saveUser(UserInfoSso userInfoSso);

    /**
     * 获取管理员用户
     */
    List<QywxUser> getAdminList(String corpId);

    /**
     * 保存管理员信息
     */
    void saveAdminInfo(String corpId, List<ApplicationAdmin> applicationAdminList);


}
