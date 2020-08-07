package com.linksteady.operate.dao;

import com.linksteady.common.domain.Ztree;
import com.linksteady.operate.domain.AddUserConfig;
import com.linksteady.operate.domain.AddUserHead;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/7/16
 */
public interface AddUserMapper {

    int getHeadCount();

    List<AddUserHead> getHeadPageList(int limit, int offset);

    void saveHeadData(AddUserHead addUserHead);

    void saveConfigData(AddUserConfig addUserConfig);

    void deleteHead(String id);

    void deleteConfig(String headId);

    AddUserConfig getConfigByHeadId(String headId);

    void editConfig(AddUserConfig addUserConfig);

    List<Map<String, String>> getSource();
}
