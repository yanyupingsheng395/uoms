package com.linksteady.system.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.linksteady.common.util.FileUtils;
import com.linksteady.system.dao.QywxDeptAndUserMapper;
import com.linksteady.system.service.QywxDeptAndUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class QywxDeptAndUserServiceImpl implements QywxDeptAndUserService {

	@Autowired
	private QywxDeptAndUserMapper qywxDeptAndUserMapper;

	@Override
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public void uploadData(MultipartFile file, String userName) {
		StringBuffer fileContent = new StringBuffer();
		BufferedReader bufferedReader = null;
		try {
			File tmpFile = FileUtils.multipartFileToFile(file);
			if(tmpFile == null) {
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
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("文件解析异常！");
		}finally {
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

	private List<Map> getUserList(String json, String userName) {
		JSONObject jsonObject = JSONObject.parseObject(json);
		String userJson = jsonObject.getString("user");
		String corpId = jsonObject.getString("corpId");
		List<Map> maps = JSONArray.parseArray(userJson, Map.class);
		maps.forEach(x->{
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
		maps.forEach(x->{
			x.put("corpId", corpId);
			x.put("insertBy", userName);
		});
		return maps;
	}
}
