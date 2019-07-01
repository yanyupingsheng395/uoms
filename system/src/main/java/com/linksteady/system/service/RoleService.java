package com.linksteady.system.service;

import java.util.List;

import com.linksteady.common.bo.UserRoleBo;
import com.linksteady.common.domain.Role;
import com.linksteady.common.domain.RoleWithMenu;
import com.linksteady.common.domain.Tree;

public interface RoleService extends IService<Role> {

	List<Role> findUserRole(String userName);

	List<Role> findAllRole(Role role);

	List<Role> findAllRole();
	
	RoleWithMenu findRoleWithMenus(Long roleId);

	Role findByName(String roleName);

	void addRole(Role role, Long[] menuIds);
	
	void updateRole(Role role, Long[] menuIds);

	void deleteRoles(String roleIds);

	Tree<UserRoleBo> getUserRoleTree(String roleId);
}
