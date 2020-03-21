package com.linksteady.common.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.common.domain.User;
import com.linksteady.common.domain.UserWithRole;

import java.util.List;
import java.util.Map;

public interface UserMapper extends MyMapper<User> {

	List<User> findUsers(User user);
	
	List<UserWithRole> findUserWithRole(Long userId);
	
	User findUserProfile(User user);

	Long getSeqId();

	void resetPassword(String userId, String password, String modifyName);

	List<Map<String, Object>> findAllUser();

    String getDefaultPwd();
}