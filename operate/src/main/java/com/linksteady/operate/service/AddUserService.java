package com.linksteady.operate.service;

import com.linksteady.operate.domain.AddUserConfig;
import com.linksteady.operate.domain.AddUserHead;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/7/16
 */
public interface AddUserService {

    int getHeadCount();

    List<AddUserHead> getHeadPageList(int limit, int offset);

    void saveData(AddUserConfig addUserConfig);

    void deleteTask(String id);

    AddUserConfig getConfigByHeadId(String id);

    void editConfig(AddUserConfig addUserConfig);
}
