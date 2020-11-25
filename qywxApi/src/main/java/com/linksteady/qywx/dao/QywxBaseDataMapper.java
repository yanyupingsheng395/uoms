package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxDeptUser;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/8/27
 */
public interface QywxBaseDataMapper {

    List<Map<String, Object>> getFollowUserList(Integer limit, Integer offset);

    List<Map<String, Object>> getDeptList(Integer limit, Integer offset);

    int getFollowUserCount();

    int getDeptCount();

    List<QywxDeptUser> getDeptAndUserData();

    List<String> getUserIdsByDeptId(List<String> deptIds);

    void getQywxDept();

    List<Map<String, Object>> getUserList();
}
