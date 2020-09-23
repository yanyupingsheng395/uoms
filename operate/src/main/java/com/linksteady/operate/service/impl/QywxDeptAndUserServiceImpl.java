package com.linksteady.operate.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.domain.Menu;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.FileUtils;
import com.linksteady.common.util.TreeUtils;
import com.linksteady.operate.dao.QywxDeptAndUserMapper;
import com.linksteady.operate.domain.QywxDeptAndUser;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.QywxDeptAndUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QywxDeptAndUserServiceImpl implements QywxDeptAndUserService {

    @Autowired
    private QywxDeptAndUserMapper qywxDeptAndUserMapper;

    @Autowired
    private ConfigService configService;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void uploadData(MultipartFile file, String userName) {
        StringBuffer fileContent = new StringBuffer();
        BufferedReader bufferedReader = null;
        try {
            File tmpFile = FileUtils.multipartFileToFile(file);
            if (tmpFile == null) {
                throw new RuntimeException("上传的文件为空！");
            }
            bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            FileUtils.deleteTempFile(tmpFile);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileContent.append(new String(line.getBytes(), StandardCharsets.UTF_8));
            }
            List<Map> userList = getUserList(fileContent.toString(), userName);
            List<Map> deptList = getDeptList(fileContent.toString(), userName);
            qywxDeptAndUserMapper.saveUserList(userList);
            qywxDeptAndUserMapper.saveDeptList(deptList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件解析异常！");
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Map<String, Object>> getUserTableData(Integer limit, Integer offset) {
        return qywxDeptAndUserMapper.getUserTableData(limit, offset);
    }

    @Override
    public List<Map<String, Object>> getDeptTableData(Integer limit, Integer offset) {
        return qywxDeptAndUserMapper.getDeptTableData(limit, offset);
    }

    @Override
    public int getUserTableCount() {
        return qywxDeptAndUserMapper.getUserTableCount();
    }

    @Override
    public int getDeptTableCount() {
        return qywxDeptAndUserMapper.getDeptTableCount();
    }

    @Override
    public Tree<QywxDeptAndUser> getDeptAndUserTree() throws LinkSteadyException {
        String corpId = configService.getValueByName(ConfigEnum.qywxCorpId.getKeyCode());
        List<QywxDeptAndUser> deptAndUsers = qywxDeptAndUserMapper.getDeptAndUserData(corpId);
        List<QywxDeptAndUser> tmpList = deptAndUsers.stream().filter(x -> StringUtils.isNotEmpty(x.getDeptId()) && StringUtils.isNotEmpty(x.getDeptName()) && StringUtils.isNotEmpty(x.getDeptParentId()))
                .collect(Collectors.toList());
        if(deptAndUsers.size() == 0 || tmpList.size() == 0) {
            throw new LinkSteadyException();
        }
        LinkedHashSet<Tree<QywxDeptAndUser>> trees = new LinkedHashSet<>();
        buildTrees(trees, deptAndUsers);
        return TreeUtils.build(new ArrayList<>(trees));
    }

    private void buildTrees(LinkedHashSet<Tree<QywxDeptAndUser>> trees, List<QywxDeptAndUser> deptAndUsers) {
        deptAndUsers.forEach(tmp -> {
            Tree<QywxDeptAndUser> deptTree = new Tree<>();
            Tree<QywxDeptAndUser> userTree = new Tree<>();
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

    private List<Map> getUserList(String json, String userName) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String userJson = jsonObject.getString("user");
        String corpId = jsonObject.getString("corpId");
        List<Map> maps = JSONArray.parseArray(userJson, Map.class);
        maps.forEach(x -> {
            x.put("corpId", corpId);
            x.put("insertBy", userName);
        });
        return maps;
    }

    private List<Map> getDeptList(String json, String userName) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String deptJson = jsonObject.getString("dept");
        String corpId = jsonObject.getString("corpId");
        List<Map> maps = JSONArray.parseArray(deptJson, Map.class);
        maps.forEach(x -> {
            x.put("corpId", corpId);
            x.put("insertBy", userName);
        });
        return maps;
    }
}
