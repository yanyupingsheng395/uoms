package com.linksteady.system.service;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.User;
import com.linksteady.common.service.IService;
import com.linksteady.system.domain.UserWithRole;
import java.util.List;

public interface UserService extends IService<User> {

    UserWithRole findById(Long userId);

    User findByName(String userName,Long userId);

    User findByName(String userName);

    List<User> findUsers(User user, QueryRequest request);

    void addUser(User user, Long[] roles) throws Exception;

    void setUserRoles(User user, Long[] roles,String currUser);

    void updateUser(User user, Long[] roles);

    void deleteUsers(String userIds);

    void updateLoginTime(String userName);

    User findUserProfile(Long userId);

    void resetPassword(Long userId);

    String getDefaultPwd();

    List<User> findAllUser();

    void logLoginEvent(String userName, String operation);
}
