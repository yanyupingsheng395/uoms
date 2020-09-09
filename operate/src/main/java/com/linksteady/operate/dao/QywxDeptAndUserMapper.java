package com.linksteady.operate.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/8/27
 */
public interface QywxDeptAndUserMapper {

    void saveUserList(@Param("userList") List<Map> userList);

    void saveDeptList(@Param("deptList") List<Map> deptList);

    List<Map<String, Object>> getUserTableData(Integer limit, Integer offset);

    List<Map<String, Object>> getDeptTableData(Integer limit, Integer offset);

    int getUserTableCount();

    int getDeptTableCount();
}
