package com.linksteady.system.dao;

import com.linksteady.common.bo.UserRoleBo;
import com.linksteady.common.config.MyMapper;
import com.linksteady.system.domain.UserRole;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface UserRoleMapper extends MyMapper<UserRole> {

    /**
     * 给用户授权时，获取用户树
     * @param roleId
     * @return
     */
    List<UserRoleBo> findUserRole(@Param("roleId") Long roleId);

    void insertDataList(List<UserRole> userRoles);
}