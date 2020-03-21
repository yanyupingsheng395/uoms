package com.linksteady.common.dao;

import com.linksteady.common.bo.UserRoleBo;
import com.linksteady.common.config.MyMapper;
import com.linksteady.common.domain.UserRole;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface UserRoleMapper extends MyMapper<UserRole> {

    /**
     * 给用户授权时，获取用户树
     * @param roleId
     * @return
     */
    List<UserRoleBo> findUserRole(@Param("roleId") String roleId);

    void insertDataList(List<UserRole> userRoles);
}