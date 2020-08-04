package com.linksteady.system.service;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.User;
import com.linksteady.system.domain.UserWithRole;
import com.linksteady.common.service.IService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {

    UserWithRole findById(Long userId);

    User findByName(String userName,Long userId);

    User findByName(String userName);

    List<User> findUsers(User user, QueryRequest request);

    void updateTheme(String theme, String userName);

    void addUser(User user, Long[] roles) throws Exception;

    void setUserRoles(User user, Long[] roles,String currUser);

    void updateUser(User user, Long[] roles);

    void deleteUsers(String userIds);

    void updateLoginTime(String userName);

    User findUserProfile(Long userId);

    void updateUserProfile(User user);

    void resetPassword(Long userId);

    String getDefaultPwd();

    List<User> findAllUser();
}
