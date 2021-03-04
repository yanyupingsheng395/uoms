package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.exception.WxErrorException;

import java.util.List;

/**
 * 成员相关的服务类
 */
public interface FollowUserService {

    List<String> callFollowUserRemote() throws WxErrorException;

    FollowUser callFollowUserDetailRemote(String followerUserId) throws WxErrorException;

    void syncQywxFollowUser() throws Exception;

    void syncDept() throws Exception;

    List<FollowUser> getFollowUserList();
}
