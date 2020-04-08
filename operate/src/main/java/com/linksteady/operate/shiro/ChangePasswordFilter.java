package com.linksteady.operate.shiro;

import com.linksteady.common.bo.UserBo;
import com.linksteady.common.domain.User;
import com.linksteady.operate.config.SystemProperties;
import com.linksteady.operate.util.SpringContextUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 首次登陆用户强制修改密码过滤器
 */
public class ChangePasswordFilter extends AccessControlFilter {


    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        return false;

    }

    /**
     * onAccessDenied：表示当访问拒绝时是否已经处理了；如果返回true表示需要继续处理；如果返回false表示该拦截器实例已经处理了，将直接返回即可
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        SystemProperties systemProperties=(SystemProperties) SpringContextUtils.getBean("systemProperties");

        Subject subject = getSubject(servletRequest, servletResponse);
        // 表示没有登录，重定向到登录页面
        if (subject.getPrincipal() == null) {
            saveRequest(servletRequest);
            WebUtils.issueRedirect(servletRequest, servletResponse, systemProperties.getShiro().getLoginUrl());
            return false;
        } else {
            UserBo userBo = (UserBo) subject.getPrincipal();
            // 如果首次登录未修改密码，则跳转到修改密码页面
            if (systemProperties.getShiro().isAllowResetPassword()&&"Y".equals(userBo.getFirstLogin())) {
                WebUtils.issueRedirect(servletRequest, servletResponse, systemProperties.getShiro().getResetPasswordUrl());
                return false;
            }
            return true;
        }
    }
    
}
