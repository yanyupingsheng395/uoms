package com.linksteady.operate.service.impl;

import com.linksteady.common.domain.Ztree;
import com.linksteady.operate.dao.AddUserMapper;
import com.linksteady.operate.domain.AddUserConfig;
import com.linksteady.operate.domain.AddUserHead;
import com.linksteady.operate.service.AddUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/7/16
 */
@Service
public class AddUserServiceImpl implements AddUserService {

    @Autowired
    private AddUserMapper addUserMapper;

    @Override
    public int getHeadCount() {
        return addUserMapper.getHeadCount();
    }

    @Override
    public List<AddUserHead> getHeadPageList(int limit, int offset) {
        return addUserMapper.getHeadPageList(limit, offset);
    }

    @Override
    public void saveData(AddUserConfig addUserConfig) {
        AddUserHead addUserHead = new AddUserHead();
        addUserHead.setTaskStatus("edit");
        addUserMapper.saveHeadData(addUserHead);
        addUserConfig.setHeadId(addUserHead.getId());
        addUserMapper.saveConfigData(addUserConfig);
    }

    @Override
    public void deleteTask(String id) {
        addUserMapper.deleteHead(id);
        addUserMapper.deleteConfig(id);
    }

    @Override
    public AddUserConfig getConfigByHeadId(String id) {
        return addUserMapper.getConfigByHeadId(id);
    }

    @Override
    public void editConfig(AddUserConfig addUserConfig) {
        addUserMapper.editConfig(addUserConfig);
    }

    @Override
    public List<Map<String, String>> getSource() {
        return addUserMapper.getSource();
    }
}
