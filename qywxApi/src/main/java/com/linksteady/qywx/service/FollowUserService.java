package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.exception.WxErrorException;

import java.util.List;

/**
 * 成员相关的服务类
 */
public interface FollowUserService {

    List<String> selectFollowUserList() throws WxErrorException;

    FollowUser selectUserDetail(String followerUserId) throws WxErrorException;

    void syncQywxFollowUser() throws Exception;
}
