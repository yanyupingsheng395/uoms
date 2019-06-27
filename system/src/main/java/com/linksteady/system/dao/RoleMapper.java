package com.linksteady.system.dao;

import java.util.List;

import com.linksteady.system.config.MyMapper;
import com.linksteady.common.domain.Role;
import com.linksteady.common.domain.RoleWithMenu;

public interface RoleMapper extends MyMapper<Role> {
	
	List<Role> findUserRole(String userName);
	
	List<RoleWithMenu> findById(Long roleId);
}