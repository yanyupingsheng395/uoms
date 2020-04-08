package com.linksteady.system.service;

import com.linksteady.system.domain.UserRole;
import com.linksteady.common.service.IService;

public interface UserRoleService extends IService<UserRole> {

	void deleteUserRolesByRoleId(String roleIds);

	void deleteUserRolesByUserId(String userIds);

	void updateUserRole(String userIds, String roleId);
}
