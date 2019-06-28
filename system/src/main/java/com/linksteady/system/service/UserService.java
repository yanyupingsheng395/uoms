package com.linksteady.system.service;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.User;
import com.linksteady.common.domain.UserWithRole;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@CacheConfig(cacheNames = "UserService")
public interface UserService extends IService<User> {

    UserWithRole findById(Long userId);

    User findByName(String userName);

    @Cacheable(key = "#p0.toString()+ #p1.toString()")
    List<User> findUsers(User user, QueryRequest request);

    @CacheEvict(key = "#p0", allEntries = true)
    void registUser(User user);

    void updateTheme(String theme, String userName);

    @CacheEvict(allEntries = true)
    void addUser(User user, Long[] roles) throws Exception;

    @CacheEvict(key = "#p0", allEntries = true)
    void updateUser(User user, Long[] roles);

    @CacheEvict(key = "#p0", allEntries = true)
    void deleteUsers(String userIds);

    void updateLoginTime(String userName);

    void updatePassword(String password);

    void updatePassword(String username, String password);

    User findUserProfile(User user);

    void updateUserProfile(User user);

    @CacheEvict(key = "#p0", allEntries = true)
    void resetPassword(String userId);
}
