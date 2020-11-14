package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.FollowUser;

import java.util.List;

public interface FollowUserMapper {

    /**
     * 更新外部联系人
     * @param followUser
     */
    void updateFollowUser(FollowUser followUser);

    /**
     * 保存导购信息
     */
    void saveFollowerUser(List<String> followerUserList);

    void updateDeleteFlag();

    void deleteFollowUser();


}