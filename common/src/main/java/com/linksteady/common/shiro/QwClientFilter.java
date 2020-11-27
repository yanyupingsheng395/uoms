package com.linksteady.common.shiro;

import com.linksteady.common.bo.UserBo;
import com.linksteady.common.config.ShiroProperties;
import com.linksteady.common.constant.CommonConstant;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.service.CommonFunService;
import com.linksteady.common.util.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 企业微信客户端判断用户是否登录的过滤器
 */
@Slf4j
public class QwClientFilter extends AccessControlFilter {


    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);
        //企业微信进行oauth认证的地址
        CommonFunService commonFunService= (CommonFunService) SpringContextUtils.getBean("commonFunServiceImpl");

        SysInfoBo sysInfoBo=commonFunService.getSysInfoByCode(CommonConstant.SYS_CODE);

        if(null==sysInfoBo|| StringUtils.isEmpty(sysInfoBo.getSysDomain()))
        {
            WebUtils.issueRedirect(servletRequest, servletResponse,"error/403");
            return false;
        }

        // 表示没有登录，重定向到登录页面
        if (subject.getPrincipal() == null) {
            saveRequest(servletRequest);
            HttpServletRequest httpRequest = WebUtils.toHttp(servletRequest);
            //获取请求的路径
            String sourceUrl=java.net.URLEncoder.encode(httpRequest.getServletPath(),"utf-8");
            log.info("QwClientFilter sourceUrl={}",sourceUrl);
            httpRequest.getSession().setAttribute("sourceUrl",sourceUrl);

            //重定向到企业微信的oauth接口
            WebUtils.issueRedirect(servletRequest, servletResponse,sysInfoBo.getSysDomain()+"/qw/oauth");
            return false;
        } else {
            return true;
        }
    }
}
