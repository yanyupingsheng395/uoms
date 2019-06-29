package com.linksteady.system.shiro;

import com.linksteady.common.domain.User;
import com.linksteady.common.util.SpringContextUtils;
import com.linksteady.system.config.SystemProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author hxcao
 * @date 2019-06-29
 */
public class RedirectMainFilter extends AccessControlFilter {

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
        String requestUrl= WebUtils.getPathWithinApplication(WebUtils.toHttp(servletRequest));
        Subject subject = getSubject(servletRequest, servletResponse);
        // 表示没有登录，重定向到登录页面
        if (subject.getPrincipal() == null) {
            saveRequest(servletRequest);
            WebUtils.issueRedirect(servletRequest, servletResponse, systemProperties.getShiro().getLoginUrl());
            return false;
        } else {
            User user = (User) subject.getPrincipal();
            HttpSession session = WebUtils.toHttp(servletRequest).getSession();
            String sysId = String.valueOf(session.getAttribute("sysId"));
            if (StringUtils.isEmpty(sysId) || "null".equals(sysId)) {
                WebUtils.issueRedirect(servletRequest, servletResponse, systemProperties.getShiro().getSuccessUrl());
            }
        }
        return true;
    }
}
