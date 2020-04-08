package com.linksteady.system.service;

import com.linksteady.common.bo.UserRoleBo;
import com.linksteady.system.domain.Role;
import com.linksteady.system.domain.RoleWithMenu;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.service.IService;

import java.util.List;

public interface RoleService extends IService<Role> {

	List<Role> findUserRole(long userId);

	List<Role> findAllRole(Role role);

	List<Role> findAllRole();
	
	RoleWithMenu findRoleWithMenus(Long roleId);

	Role findByName(String roleName);

	void addRole(Role role, Long[] menuIds);
	
	void updateRole(Role role, Long[] menuIds);

	void deleteRoles(String roleIds);

	Tree<UserRoleBo> getUserRoleTree(String roleId);
}
