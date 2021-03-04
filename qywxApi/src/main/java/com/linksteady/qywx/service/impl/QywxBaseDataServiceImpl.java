package com.linksteady.qywx.service.impl;


import com.linksteady.qywx.dao.FollowUserMapper;
import com.linksteady.qywx.dao.QywxDeptMapper;
import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.service.QywxBaseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QywxBaseDataServiceImpl implements QywxBaseDataService {

    @Autowired(required = false)
    private FollowUserMapper followUserMapper;

    @Autowired
    QywxDeptMapper qywxDeptMapper;


    @Override
    public List<FollowUser> getFollowUserList(Integer limit, Integer offset) {
        return followUserMapper.getFollowUserListPagging(limit, offset);
    }

    @Override
    public List<Map<String, Object>> getDeptList(Integer limit, Integer offset) {
        return qywxDeptMapper.getDeptList(limit, offset);
    }

    @Override
    public int getFollowUserCount() {
        return followUserMapper.getFollowUserCount();
    }

    @Override
    public int getDeptCount() {
        return qywxDeptMapper.getDeptCount();
    }

    @Override
    public List<Map<String, Object>> getDept() {
        return qywxDeptMapper.getDeptList(0, 0);//查询部门
    }

    @Override
    public List<FollowUser> getFollowUserList() {
        return followUserMapper.getFollowUserList();
    }
}
