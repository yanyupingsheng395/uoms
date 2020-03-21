package com.linksteady.common.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.common.domain.Role;
import com.linksteady.common.domain.RoleWithMenu;

import java.util.List;

public interface RoleMapper extends MyMapper<Role> {
	
	List<Role> findUserRole(String userName);
	
	List<RoleWithMenu> findById(Long roleId);

	List<Role> findAll();
}