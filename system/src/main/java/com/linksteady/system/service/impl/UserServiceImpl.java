package com.linksteady.system.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.util.MD5Utils;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.linksteady.system.dao.UserMapper;
import com.linksteady.system.dao.UserRoleMapper;
import com.linksteady.common.domain.User;
import com.linksteady.common.domain.UserRole;
import com.linksteady.common.domain.UserWithRole;
import com.linksteady.system.service.UserRoleService;
import com.linksteady.system.service.UserService;
import tk.mybatis.mapper.entity.Example;

@Service("userService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
@Slf4j
public class UserServiceImpl extends BaseService<User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Override
    public User findByName(String userName) {
        Example example = new Example(User.class);
        example.createCriteria().andCondition("lower(username)=", userName.toLowerCase());
        List<User> list = this.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<User> findUsers(User user, QueryRequest request) {
        try {
            return this.userMapper.findUsers(user);
        } catch (Exception e) {
            log.error("error", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registUser(User user) {
        user.setCrateTime(new Date());
        user.setTheme(User.DEFAULT_THEME);
        user.setAvatar(User.DEFAULT_AVATAR);
        user.setSsex(User.SEX_UNKNOW);
        user.setPassword(MD5Utils.encrypt(user.getUsername(), user.getPassword()));
        this.save(user);
        UserRole ur = new UserRole();
        ur.setUserId(user.getUserId());
        ur.setRoleId(3L);
        this.userRoleMapper.insert(ur);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTheme(String theme, String userName) {
        Example example = new Example(User.class);
        example.createCriteria().andCondition("username=", userName);
        User user = new User();
        user.setTheme(theme);
        this.userMapper.updateByExampleSelective(user, example);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(User user, Long[] roles) throws Exception{
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();

        Long id = userMapper.getSeqId();
        user.setUserId(id);
        user.setCrateTime(new Date());
        user.setPassword(MD5Utils.encrypt(user.getUsername(), user.getPassword()));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(null==user.getExpire()||"".equals(user.getExpire()))
        {
            user.setExpireDate(Date.from(LocalDate.parse("2999-12-31",dtf).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }else
        {
            user.setExpireDate(Date.from(LocalDate.parse(user.getExpire(),dtf).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        user.setCreateBy(currentUser.getUsername());
        this.save(user);
        setUserRoles(user, roles);
    }

    private void setUserRoles(User user, Long[] roles) {
        Arrays.stream(roles).forEach(roleId -> {
            UserRole ur = new UserRole();
            ur.setUserId(user.getUserId());
            ur.setRoleId(roleId);
            this.userRoleMapper.insert(ur);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user, Long[] roles) {
        user.setPassword(null);
        user.setUsername(null);
        user.setModifyTime(new Date());
        user.setUpdateBy(((User) SecurityUtils.getSubject().getPrincipal()).getUsername());
        this.updateNotNull(user);
        Example example = new Example(UserRole.class);
        example.createCriteria().andCondition("user_id=", user.getUserId());
        this.userRoleMapper.deleteByExample(example);
        setUserRoles(user, roles);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUsers(String userIds) {
        List<String> list = Arrays.asList(userIds.split(","));
        this.batchDelete(list, "userId", User.class);

        this.userRoleService.deleteUserRolesByUserId(userIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLoginTime(String userName) {
        Example example = new Example(User.class);
        example.createCriteria().andCondition("lower(username)=", userName.toLowerCase());
        User user = new User();
        user.setLastLoginTime(new Date());
        this.userMapper.updateByExampleSelective(user, example);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(String password) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Example example = new Example(User.class);
        example.createCriteria().andCondition("username=", user.getUsername());
        String newPassword = MD5Utils.encrypt(user.getUsername().toLowerCase(), password);
        user.setPassword(newPassword);
        user.setFirstLogin("N");
        this.userMapper.updateByExampleSelective(user, example);
    }

    /**
     * RPC调用更改密码的服务，由于无法获取当前登录的账号信息，所以重载该方法
     * @param username
     * @param password
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(String username, String password) {
        User user = this.findByName(username);
        Example example = new Example(User.class);
        example.createCriteria().andCondition("username=", user.getUsername());
        String newPassword = MD5Utils.encrypt(user.getUsername().toLowerCase(), password);
        user.setPassword(newPassword);
        user.setFirstLogin("N");
        this.userMapper.updateByExampleSelective(user, example);
    }

    @Override
    public UserWithRole findById(Long userId) {
        List<UserWithRole> list = this.userMapper.findUserWithRole(userId);
        List<Long> roleList = new ArrayList<>();
        for (UserWithRole uwr : list) {
            roleList.add(uwr.getRoleId());
        }
        if (list.isEmpty()) {
            return null;
        }
        UserWithRole userWithRole = list.get(0);
        userWithRole.setRoleIds(roleList);
        if(userWithRole.getExpireDate() != null) {
            userWithRole.setExpire(new SimpleDateFormat("yyyy-MM-dd").format(userWithRole.getExpireDate()));
        }
        return userWithRole;
    }

    @Override
    public User findUserProfile(User user) {
        return this.userMapper.findUserProfile(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserProfile(User user) {
        user.setUsername(null);
        user.setPassword(null);
        this.updateNotNull(user);
    }

    @Override
    public void resetPassword(String userId) {
        User currentUser = (User) SecurityUtils.getSubject().getPrincipal();

        //获取用户名
        User user=this.selectByKey(userId);

        String password=MD5Utils.encrypt(user.getUsername(),user.getUsername()+"123");
        this.userMapper.resetPassword(userId,password,currentUser.getUsername());
    }


}
