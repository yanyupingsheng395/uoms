package com.linksteady.system.service.impl;

import com.linksteady.common.bo.UserBo;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.system.dao.UserMapper;
import com.linksteady.system.dao.UserRoleMapper;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.User;
import com.linksteady.system.domain.UserRole;
import com.linksteady.system.domain.UserWithRole;
import com.linksteady.common.service.impl.BaseService;
import com.linksteady.common.util.MD5Utils;
import com.linksteady.system.service.UserRoleService;
import com.linksteady.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    public User findByName(String userName,Long userId) {
        Example example = new Example(User.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andLike("username","%"+userName.toLowerCase()+"%");

        if(null!=userId)
        {
            criteria.andCondition("userId",userId);
        }
        List<User> list = this.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public User findByName(String userName) {
        Example example = new Example(User.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andCondition("username=",userName.toLowerCase());
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
            exceptionNoticeHandler.exceptionNotice(e);
            return new ArrayList<>();
        }
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
    @Transactional(rollbackFor = RuntimeException.class)
    public void addUser(User user, Long[] roles){
        UserBo userBo = (UserBo) SecurityUtils.getSubject().getPrincipal();
        user.setCreateDt(new Date());
        user.setPassword(MD5Utils.encrypt(user.getUsername(), user.getPassword()));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(null==user.getExpire()||"".equals(user.getExpire()))
        {
            user.setExpireDate(Date.from(LocalDate.parse("2999-12-31",dtf).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }else
        {
            user.setExpireDate(Date.from(LocalDate.parse(user.getExpire(),dtf).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }

        user.setAvatar("default.jpg");
        user.setTheme("cyan");
        user.setCreateBy(userBo.getUsername());
        userMapper.saveUser(user);

        UserService userService=(UserService)AopContext.currentProxy();
        userService.setUserRoles(user,roles,userBo.getUsername());
    }
    @Override
    public void setUserRoles(User user, Long[] roles,String currUser) {
        Arrays.stream(roles).forEach(roleId -> {
            UserRole ur = new UserRole();
            ur.setUserId(user.getUserId());
            ur.setRoleId(roleId);
            ur.setCreateDt(new Date());
            ur.setCreateBy(currUser);
            this.userRoleMapper.insert(ur);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user, Long[] roles) {
        UserBo userBo=(UserBo) SecurityUtils.getSubject().getPrincipal();
        user.setPassword(null);
        user.setUsername(null);
        user.setFirstLogin(null);
        user.setUpdateDt(new Date());
        user.setUpdateBy(userBo.getUsername());
        String expire=user.getExpire();
        if(StringUtils.isNotEmpty(expire))
        {
            try {
                Date expireDate=new SimpleDateFormat("yyyy-MM-dd").parse(expire);
                user.setExpireDate(expireDate);
            } catch (ParseException e) {
                log.error("系统管理更新用户失效日期转换错误");
            }
        }

        this.updateNotNull(user);
        Example example = new Example(UserRole.class);
        example.createCriteria().andCondition("user_id=", user.getUserId());
        this.userRoleMapper.deleteByExample(example);
        setUserRoles(user, roles,userBo.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUsers(String userIds) {
        List<Long> list = Arrays.asList(userIds.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
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
    public User findUserProfile(Long userId) {
        return this.userMapper.selectByPrimaryKey(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserProfile(User user) {
        user.setUsername(null);
        user.setPassword(null);
        this.updateNotNull(user);
    }

    @Override
    public void resetPassword(Long userId) {
        UserBo userBo = (UserBo) SecurityUtils.getSubject().getPrincipal();
        //获取用户名
        User user=this.selectByKey(userId);

        String defaultPwd = userMapper.getDefaultPwd();
        String password=MD5Utils.encrypt(user.getUsername(),defaultPwd);
        this.userMapper.resetPassword(userId,password,userBo.getUsername());
    }

    @Override
    public List<User> findAllUser() {
        return this.userMapper.findAllUser();
    }

    @Override
    public String getDefaultPwd() {
        return userMapper.getDefaultPwd();
    }
}
