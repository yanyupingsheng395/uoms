package com.linksteady.system.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        CustomUsernamePasswordToken customUsernamePasswordToken = (CustomUsernamePasswordToken) token;
        //如果是企业微信免密登录直接返回true
        if("QYWX".equals(customUsernamePasswordToken.getLoginType())){
            return true;
        }
        //不是免密登录，调用父类的方法
        return super.doCredentialsMatch(customUsernamePasswordToken, info);
    }
}
