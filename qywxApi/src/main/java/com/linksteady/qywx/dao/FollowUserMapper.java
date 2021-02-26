package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.FollowUser;

import java.util.List;
import java.util.Map;

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

    void updateDeptDeleteFlag();

    void saveDept(Long id,String deptName,Long parentId,int orderNo);

    void deleteDept();

    List<Map<String, Object>> getUserList();

    int getFollowUserCount();


}