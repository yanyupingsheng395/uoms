package com.linksteady.system.dao;

import java.util.List;

import com.linksteady.system.config.MyMapper;
import com.linksteady.common.domain.User;
import com.linksteady.common.domain.UserWithRole;

public interface UserMapper extends MyMapper<User> {

	List<User> findUsers(User user);
	
	List<UserWithRole> findUserWithRole(Long userId);
	
	User findUserProfile(User user);

	Long getSeqId();

	void resetPassword(String userId,String password,String modifyName);
}