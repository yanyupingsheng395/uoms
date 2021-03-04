package com.linksteady.qywx.service;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.domain.Tree;
import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.domain.QywxDeptUser;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/8/27
 */
public interface QywxBaseDataService {

    List<FollowUser> getFollowUserList(Integer limit, Integer offset);

    List<Map<String, Object>> getDeptList(Integer limit, Integer offset);

    int getFollowUserCount();

    int getDeptCount();

    List<Map<String, Object>> getDept();

    List<FollowUser> getFollowUserList();
}
