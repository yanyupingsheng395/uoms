package com.linksteady.operate.shiro;

import com.alibaba.fastjson.JSON;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.HttpUtils;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**(
 * 自定义过滤器
 * @author internet
 */
public class CustomUserFilter extends UserFilter {

	/**
	 * 判断是否是 ajax 请求
	 * 如果是，则返回 401 状态码
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		if (HttpUtils.isAjaxRequest((HttpServletRequest) request)) {
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			//http的状态码为401
			httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			httpServletResponse.setContentType("application/json; charset=utf-8");
			httpServletResponse.getWriter().print(JSON.toJSON(ResponseBo.error("")));
			return false;
		} else {
			saveRequestAndRedirectToLogin(request, response);
			return false;
		}
	}
}
