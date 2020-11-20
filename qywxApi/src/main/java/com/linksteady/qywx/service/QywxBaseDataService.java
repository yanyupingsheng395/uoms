package com.linksteady.qywx.service;

import com.linksteady.common.domain.Tree;
import com.linksteady.qywx.domain.QywxDeptUser;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/8/27
 */
public interface QywxBaseDataService {

    List<Map<String, Object>> getFollowUserList(Integer limit, Integer offset);

    List<Map<String, Object>> getDeptList(Integer limit, Integer offset);

    int getFollowUserCount();

    int getDeptCount();

    Tree<QywxDeptUser> getDeptAndUserTree() throws Exception;
}
