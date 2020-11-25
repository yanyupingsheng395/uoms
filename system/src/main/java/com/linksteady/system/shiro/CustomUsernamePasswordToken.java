package com.linksteady.system.shiro;

import lombok.Data;
import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 自定义token 实现用户名、密码登录和企业微信登录
 */
@Data
public class CustomUsernamePasswordToken extends UsernamePasswordToken {

    private String loginType;

    /**
     * 免密登录
     */
    public CustomUsernamePasswordToken(String username,String loginType) {
        super(username, "", false, null);
        this.loginType = loginType;
    }
    /**
     * 账号密码登录
     */
    public CustomUsernamePasswordToken(String username, String password,String loginType) {
        super(username, password, false, null);
        this.loginType = loginType;
    }
}
