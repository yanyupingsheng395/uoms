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

    List<Map<String, Object>> getUserTableData(Integer limit, Integer offset);

    List<Map<String, Object>> getDeptTableData(Integer limit, Integer offset);

    int getUserTableCount();

    int getDeptTableCount();

    Tree<QywxDeptUser> getDeptAndUserTree() throws Exception;
}
