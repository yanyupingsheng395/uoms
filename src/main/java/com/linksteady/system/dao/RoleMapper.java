package com.linksteady.system.dao;

import java.util.List;

import com.linksteady.common.config.MyMapper;
import com.linksteady.system.domain.Role;
import com.linksteady.system.domain.RoleWithMenu;

public interface RoleMapper extends MyMapper<Role> {
	
	List<Role> findUserRole(String userName);
	
	List<RoleWithMenu> findById(Long roleId);
}