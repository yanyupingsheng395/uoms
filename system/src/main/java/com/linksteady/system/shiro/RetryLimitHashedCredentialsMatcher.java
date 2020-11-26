package com.linksteady.system.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;

@Slf4j
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        CustomUsernamePasswordToken customUsernamePasswordToken = (CustomUsernamePasswordToken) token;
        //如果是企业微信免密登录直接返回true
        if("QYWX".equals(customUsernamePasswordToken.getLoginType())){
            return true;
        }
        //不是免密登录，调用父类的方法
        boolean flag=super.doCredentialsMatch(customUsernamePasswordToken, info);
        return flag;
    }
}
