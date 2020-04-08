package com.linksteady.system.shiro;

import com.linksteady.common.bo.UserBo;
import com.linksteady.common.domain.Menu;
import com.linksteady.common.domain.User;
import com.linksteady.system.service.MenuService;
import com.linksteady.system.service.RoleService;
import com.linksteady.system.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 自定义实现 ShiroRealm，包含认证和授权两大模块
 *
 * @author MrBird
 */
public class ShiroRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;

    /**
     * 授权模块，获取用户角色和权限
     *
     * @param principal principal
     * @return AuthorizationInfo 权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        UserBo userBo = (UserBo) SecurityUtils.getSubject().getPrincipal();

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(userBo.getPermission());
        return simpleAuthorizationInfo;
    }

    /**
     * 用户认证
     *
     * @param token AuthenticationToken 身份认证 token
     * @return AuthenticationInfo 身份认证信息
     * @throws AuthenticationException 认证相关异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        // 获取用户输入的用户名和密码
        String userName = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());

        // 通过用户名到数据库查询用户信息
        User user = this.userService.findByName(userName);
        if (user == null) {
            throw new UnknownAccountException("用户名或密码错误！");
        }
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
