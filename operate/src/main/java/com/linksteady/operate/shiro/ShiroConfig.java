package com.linksteady.operate.shiro;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.linksteady.common.config.ShiroProperties;
import com.linksteady.common.listener.ShiroSessionListener;
import com.linksteady.common.shiro.CustomUserFilter;
import com.linksteady.common.shiro.ShiroSessionManger;
import com.linksteady.common.shiro.UoShiroRealm;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import redis.clients.jedis.JedisPool;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro 配置类
 *
 * @author MrBird
 */
@Configuration
public class ShiroConfig {

    @Autowired
    private ShiroProperties shiroProperties;

    @Autowired
    JedisPool jedisPool;

    private String cipherKey="Vfixl8Hi8tXf/hS8jt2AHw==";

    /**
     * shiro 中配置 redis 缓存
     *
     * @return RedisManager
     */
    private RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setJedisPool(jedisPool);
        return redisManager;
    }

    private RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        redisCacheManager.setPrincipalIdFieldName("userId");
        return redisCacheManager;
    }

    /**
     * 设置cookie
     * @return
     */
    @Bean("sessionIdCookie")
    public SimpleCookie sessionIdCookie() {
        SimpleCookie cookie = new SimpleCookie();
        cookie.setName("JSESSIONID");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(shiroProperties.getCookieTimeout());
        cookie.setDomain(shiroProperties.getCookieDomain());
        return cookie;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        //获取filters
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        filters.put("user",new CustomUserFilter());

        // 设置 securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 登录的 url
        shiroFilterFactoryBean.setLoginUrl(shiroProperties.getLoginUrl());
        // 登录成功后跳转的 url
        shiroFilterFactoryBean.setSuccessUrl(shiroProperties.getSuccessUrl());
        // 未授权 url
        shiroFilterFactoryBean.setUnauthorizedUrl(shiroProperties.getUnauthorizedUrl());
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 设置免认证 url
        String[] anonUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(shiroProperties.getAnonUrl(), ",");
        for (String url : anonUrls) {
            filterChainDefinitionMap.put(url, "anon");
        }

        // 配置退出过滤器，其中具体的退出代码 Shiro已经替我们实现了
        filterChainDefinitionMap.put(shiroProperties.getLogoutUrl(), "logout");
        //强制修改密码的链接
        filterChainDefinitionMap.put(shiroProperties.getResetPasswordUrl(), "user");

        // 除上以外所有 url都必须认证通过才可以访问，未通过认证自动访问 LoginUrl
        filterChainDefinitionMap.put("/**", "user");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 配置 SecurityManager，并注入 shiroRealm
        securityManager.setRealm(uoShiroRealm());
        // 配置 rememberMeCookie
        securityManager.setRememberMeManager(rememberMeManager());
        // 配置 缓存管理类 cacheManager
        securityManager.setCacheManager(cacheManager());
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        // shiro 生命周期处理器
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public UoShiroRealm uoShiroRealm() {
        // 配置 Realm，需自己实现，见 ShiroRealm
        return new UoShiroRealm();
    }

    /**
     * rememberMe cookie 效果是重开浏览器后无需重新登录
     *
     * @return SimpleCookie
     */
    private SimpleCookie rememberMeCookie() {
        // 设置 cookie 名称，对应 login.html 页面的 <input type="checkbox" name="rememberMe"/>
        SimpleCookie cookie = new SimpleCookie("rememberMe");
//        cookie.setSecure(true);  // 只在 https中有效 注释掉 正常
        // 设置 cookie 的过期时间，单位为秒，这里为一天
        cookie.setMaxAge(shiroProperties.getCookieTimeout());
        return cookie;
    }

    /**
     * cookie管理对象
     *
     * @return CookieRememberMeManager
     */
    private CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        // rememberMe cookie 加密的密钥
        cookieRememberMeManager.setCipherKey(Base64.decode(cipherKey));
        return cookieRememberMeManager;
    }

    /**
     * DefaultAdvisorAutoProxyCreator 和 AuthorizationAttributeSourceAdvisor 用于开启 shiro 注解的使用
     * 如 @RequiresAuthentication， @RequiresUser， @RequiresPermissions 等
     *
     * @return DefaultAdvisorAutoProxyCreator
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreatorShiro() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 用于开启 Thymeleaf 中的 shiro 标签的使用
     *
     * @return ShiroDialect shiro 方言对象
     */
    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        //redis session对象的过期时间
        redisSessionDAO.setExpire(shiroProperties.getExpireIn());
        return redisSessionDAO;
    }

    /**
     * session 管理对象
     *
     * @return DefaultWebSessionManager
     */
    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new ShiroSessionManger();
        Collection<SessionListener> listeners = new ArrayList<>();
        listeners.add(new ShiroSessionListener());
        // 设置session超时时间，单位为毫秒
        sessionManager.setGlobalSessionTimeout(shiroProperties.getSessionTimeout());
        sessionManager.setSessionListeners(listeners);
        sessionManager.setSessionDAO(redisSessionDAO());

        sessionManager.setSessionIdUrlRewritingEnabled(false);

        sessionManager.setDeleteInvalidSessions(false);
        sessionManager.setSessionValidationSchedulerEnabled(false);

        sessionManager.setSessionIdCookie(sessionIdCookie());
        return sessionManager;
    }

}
