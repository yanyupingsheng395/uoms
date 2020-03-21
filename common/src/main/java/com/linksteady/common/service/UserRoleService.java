package com.linksteady.common.service;

import com.linksteady.common.domain.UserRole;

public interface UserRoleService extends IService<UserRole> {

	void deleteUserRolesByRoleId(String roleIds);

	void deleteUserRolesByUserId(String userIds);

	void updateUserRole(String userIds, String roleId);
}
