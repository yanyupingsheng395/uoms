package com.linksteady.qywx.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.util.TreeUtils;
import com.linksteady.qywx.dao.QywxBaseDataMapper;
import com.linksteady.qywx.domain.QywxDeptUser;
import com.linksteady.qywx.service.QywxBaseDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

@Service
public class QywxBaseDataServiceImpl implements QywxBaseDataService {

    @Autowired(required = false)
    private QywxBaseDataMapper qywxBaseDataMapper;


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
        return qywxBaseDataMapper.getFollowUserCount();
    }

    @Override
    public int getDeptCount() {
        return qywxBaseDataMapper.getDeptCount();
    }

    @Override
    public List<Map<String, Object>> getDept() throws Exception {
        return qywxBaseDataMapper.getDeptList(0, 0);//查询部门
    }

    @Override
    public List<Map<String, Object>> getUser() throws Exception {
        return qywxBaseDataMapper.getUserList();
    }

    private JSONObject toJsonObj(Map<String, String> map) {
        JSONObject jsonObject=new JSONObject();
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            jsonObject.put(key, map.get(key));
        }
        return jsonObject;
    }

    private void buildTrees(LinkedHashSet<Tree<QywxDeptUser>> trees, List<QywxDeptUser> deptAndUsers) {
        deptAndUsers.forEach(tmp -> {
            Tree<QywxDeptUser> deptTree = new Tree<>();
            Tree<QywxDeptUser> userTree = new Tree<>();
			deptTree.setId(tmp.getDeptId());
			deptTree.setParentId(tmp.getDeptParentId());
			deptTree.setText(tmp.getDeptName());
			deptTree.setIcon("mdi mdi-home mdi-16px");
			deptTree.setType("dept");
			userTree.setId(tmp.getUserId());
			userTree.setParentId(tmp.getDeptId());
			userTree.setText(tmp.getUserName());
			userTree.setIcon("mdi mdi-account mdi-16px");
			userTree.setType("user");
            trees.add(deptTree);
            trees.add(userTree);
        });
    }
}
