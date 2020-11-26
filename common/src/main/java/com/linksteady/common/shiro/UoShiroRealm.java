package com.linksteady.common.shiro;


import com.linksteady.common.bo.UserBo;
import com.linksteady.common.domain.MenuBo;
import com.linksteady.common.service.CommonFunService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 自定义实现 ShiroRealm，包含认证和授权两大模块
 *
 * @author MrBird
 */
@Component
public class UoShiroRealm extends AuthorizingRealm {

    @Lazy
    @Autowired
    private CommonFunService commonFunService;

    /**
     * 授权模块，获取用户角色和权限
     *
     * @param principal principal
     * @return AuthorizationInfo 权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        UserBo userBo = (UserBo) SecurityUtils.getSubject().getPrincipal();

        // 获取用户权限集
        List<MenuBo> permissionList = this.commonFunService.findUserPermissions(userBo.getUserId());
        Set<String> permissionSet = new HashSet<>();
        for (MenuBo m : permissionList) {
            // 处理用户多权限 用逗号分隔
            permissionSet.addAll(Arrays.asList(m.getPerms().split(",")));
        }
        userBo.setPermission(permissionSet);

        userBo.setUserMenuTree(commonFunService.getUserMenu(userBo.getUserId()));

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
        return new SimpleAuthenticationInfo();
    }

    /**
     * 清除用户缓存
     */
    public void clearCache() {
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
        super.clearCache(principals);
    }

    public void execGetAuthorizationInfo()
    {
        this.doGetAuthorizationInfo(null);
    }
}

