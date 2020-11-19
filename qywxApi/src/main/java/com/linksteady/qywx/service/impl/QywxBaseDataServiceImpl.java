package com.linksteady.qywx.service.impl;


import com.linksteady.common.domain.Tree;
import com.linksteady.common.util.TreeUtils;
import com.linksteady.qywx.dao.QywxBaseDataMapper;
import com.linksteady.qywx.domain.QywxDeptUser;
import com.linksteady.qywx.service.QywxBaseDataService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QywxBaseDataServiceImpl implements QywxBaseDataService {

    @Autowired
    private QywxBaseDataMapper qywxBaseDataMapper;


    @Override
    public List<Map<String, Object>> getUserTableData(Integer limit, Integer offset) {
        return qywxBaseDataMapper.getUserTableData(limit, offset);
    }

    @Override
    public List<Map<String, Object>> getDeptTableData(Integer limit, Integer offset) {
        return qywxBaseDataMapper.getDeptTableData(limit, offset);
    }

    @Override
    public int getUserTableCount() {
        return qywxBaseDataMapper.getUserTableCount();
    }

    @Override
    public int getDeptTableCount() {
        return qywxBaseDataMapper.getDeptTableCount();
    }

    @Override
    public Tree<QywxDeptUser> getDeptAndUserTree() throws Exception {
      //  String corpId = configService.getValueByName(ConfigEnum.qywxCorpId.getKeyCode());
        List<QywxDeptUser> deptUserList = qywxBaseDataMapper.getDeptAndUserData();
        List<QywxDeptUser> tmpList = deptUserList.stream().filter(x -> StringUtils.isNotEmpty(x.getDeptId()) && StringUtils.isNotEmpty(x.getDeptName()) && StringUtils.isNotEmpty(x.getDeptParentId()))
                .collect(Collectors.toList());
        if(deptUserList.size() == 0 || tmpList.size() == 0) {
            throw new Exception();
        }
        LinkedHashSet<Tree<QywxDeptUser>> trees = new LinkedHashSet<>();
        buildTrees(trees, deptUserList);
        return TreeUtils.build(new ArrayList<>(trees));
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
