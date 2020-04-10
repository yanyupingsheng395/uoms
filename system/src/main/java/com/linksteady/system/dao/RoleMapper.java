package com.linksteady.system.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.system.domain.Role;
import com.linksteady.system.domain.RoleWithMenu;

import java.util.List;

public interface RoleMapper extends MyMapper<Role> {
	
	List<Role> findUserRole(long userId);
	
	List<RoleWithMenu> findById(Long roleId);

	List<Role> findAll();

	void saveRole(Role role);
}