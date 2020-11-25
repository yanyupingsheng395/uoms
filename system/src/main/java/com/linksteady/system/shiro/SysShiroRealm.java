package com.linksteady.system.shiro;

import com.linksteady.common.bo.UserBo;
import com.linksteady.common.domain.Menu;
import com.linksteady.common.domain.User;
import com.linksteady.common.shiro.UoShiroRealm;
import com.linksteady.system.service.MenuService;
import com.linksteady.system.service.UserService;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.*;

/**
 * 自定义实现 ShiroRealm，包含认证和授权两大模块
 *
 * @author MrBird
 */
public class SysShiroRealm extends UoShiroRealm {

    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    private MenuService menuService;

    /**
     * 用户认证
     *
     * @param token AuthenticationToken 身份认证 token
     * @return AuthenticationInfo 身份认证信息
     * @throws AuthenticationException 认证相关异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        RetryLimitHashedCredentialsMatcher credentialsMatcher=new RetryLimitHashedCredentialsMatcher();
        setCredentialsMatcher(credentialsMatcher);

        CustomUsernamePasswordToken customUsernamePasswordToken = (CustomUsernamePasswordToken)token ;
        String userName=customUsernamePasswordToken.getUsername();
        String password="";
        User user = this.userService.findByName(userName);

        if(null==user)
        {
            throw new AuthenticationException("登录失败，不存在的用户!");
        }

        String loginType=customUsernamePasswordToken.getLoginType();
        if("QYWX".equals(loginType)&&"PASS".equals(user.getUserType()))
        {
            throw new AuthenticationException("登录失败，此用户仅允许通过用户名/密码方式登录!");
        }

        if("PASS".equals(loginType)&&"QYWX".equals(user.getUserType()))
        {
            throw new AuthenticationException("登录失败，此用户仅允许通过企业微信扫码方式登录!");
        }

        if("QYWX".equals(customUsernamePasswordToken.getLoginType()))
        {
            if (User.STATUS_LOCK.equals(user.getStatus())) {
                throw new LockedAccountException("账号已被锁定,请联系管理员！");
            }

            if (null != user.getExpireDate()){
                Calendar userDate = Calendar.getInstance();
                userDate.setTime(user.getExpireDate());
                if(Calendar.getInstance(TimeZone.getDefault()).after(userDate)) {
                    throw new LockedAccountException("账号已过期,请联系管理员！");
                }
            }
        }else
        {
            // 获取用户输入的密码
            password = new String((char[]) token.getCredentials());

            if (!password.equals(user.getPassword())) {
                throw new IncorrectCredentialsException("用户名或密码错误！");
            }
            if (User.STATUS_LOCK.equals(user.getStatus())) {
                throw new LockedAccountException("账号已被锁定,请联系管理员！");
            }

            if (null != user.getExpireDate()){
                Calendar userDate = Calendar.getInstance();
                userDate.setTime(user.getExpireDate());
                if(Calendar.getInstance(TimeZone.getDefault()).after(userDate)) {
                    throw new LockedAccountException("账号已过期,请联系管理员！");
                }
            }
        }

        // 获取用户权限集
        List<Menu> permissionList = this.menuService.findUserPermissions(user.getUserId());
        Set<String> permissionSet = new HashSet<>();
        for (Menu m : permissionList) {
            // 处理用户多权限 用逗号分隔
            permissionSet.addAll(Arrays.asList(m.getPerms().split(",")));
        }
        user.setPermissionSet(permissionSet);

        // 登录成功之后获取菜单
        user.setUserMenuTree(menuService.getUserMenu(user.getUserId()));

        UserBo userBo=new UserBo(user);
        return new SimpleAuthenticationInfo(userBo, password, getName());
    }
}
