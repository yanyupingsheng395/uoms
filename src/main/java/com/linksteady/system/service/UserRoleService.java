package com.linksteady.system.service;

import com.linksteady.common.service.IService;
import com.linksteady.system.domain.UserRole;

public interface UserRoleService extends IService<UserRole> {

	void deleteUserRolesByRoleId(String roleIds);

	void deleteUserRolesByUserId(String userIds);
}
