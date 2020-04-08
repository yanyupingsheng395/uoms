package com.linksteady.system.service;

import com.linksteady.system.domain.RoleMenu;
import com.linksteady.common.service.IService;

public interface RoleMenuServie extends IService<RoleMenu> {

	void deleteRoleMenusByRoleId(String roleIds);

	void deleteRoleMenusByMenuId(String menuIds);
}
