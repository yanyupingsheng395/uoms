package com.linksteady.system.dao;

import java.util.List;

import com.linksteady.common.config.MyMapper;
import com.linksteady.system.domain.User;
import com.linksteady.system.domain.UserWithRole;

public interface UserMapper extends MyMapper<User> {

	List<User> findUserWithDept(User user);
	
	List<UserWithRole> findUserWithRole(Long userId);
	
	User findUserProfile(User user);
}