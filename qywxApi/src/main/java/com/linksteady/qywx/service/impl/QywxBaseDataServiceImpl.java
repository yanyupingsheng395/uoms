package com.linksteady.qywx.service.impl;


import com.linksteady.qywx.dao.FollowUserMapper;
import com.linksteady.qywx.dao.QywxBaseDataMapper;
import com.linksteady.qywx.service.QywxBaseDataService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QywxBaseDataServiceImpl implements QywxBaseDataService {

    @Autowired(required = false)
    private QywxBaseDataMapper qywxBaseDataMapper;

    @Autowired(required = false)
    private FollowUserMapper followUserMapper;


    @Override
    public List<Map<String, Object>> getFollowUserList(Integer limit, Integer offset) {
        return qywxBaseDataMapper.getFollowUserList(limit, offset);
    }

    @Override
    public List<Map<String, Object>> getDeptList(Integer limit, Integer offset) {
        return qywxBaseDataMapper.getDeptList(limit, offset);
    }

    @Override
    public int getFollowUserCount() {
        return followUserMapper.getFollowUserCount();
    }

    @Override
    public int getDeptCount() {
        return qywxBaseDataMapper.getDeptCount();
    }

    @Override
    public List<Map<String, Object>> getDept() {
        return qywxBaseDataMapper.getDeptList(0, 0);//查询部门
    }

    @Override
    public List<Map<String, Object>> getUser() {
        return followUserMapper.getUserList();
    }
}
