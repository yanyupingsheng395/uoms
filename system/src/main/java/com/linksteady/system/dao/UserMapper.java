package com.linksteady.system.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.common.domain.User;
import com.linksteady.system.domain.UserWithRole;

import java.util.List;
import java.util.Map;

public interface UserMapper extends MyMapper<User> {

	List<User> findUsers(User user);
	
	List<UserWithRole> findUserWithRole(Long userId);
	
	User findUserProfile(User user);

	void resetPassword(Long userId, String password, String modifyName);

	List<User> findAllUser();

    String getDefaultPwd();

    void saveUser(User user);
}