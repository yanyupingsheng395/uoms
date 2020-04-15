package com.linksteady.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
@ConfigurationProperties(prefix = "shiro")
public class ShiroProperties {

    /**
     * shiro redis缓存时长，默认值 7200 秒
     */
    private int expireIn = 7200;
    /**
     * session 超时时间，默认 1800000毫秒
     */
    private long sessionTimeout = 7200000L;

    /**
     * rememberMe 有效时长，默认为 86400 秒，即一天
     */
    private int cookieTimeout = 86400;

    private String anonUrl;

    private String loginUrl = "/login";

    private String successUrl = "/index";

    private String logoutUrl = "/logout";

    private String unauthorizedUrl;

    private String resetPasswordUrl = "/resetPass";

    private boolean allowResetPassword = false;

    private String cookieDomain;
}
