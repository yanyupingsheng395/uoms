package com.linksteady.common.handler;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局的异常处理器
 * @author anonymous
 */
@Slf4j
@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AuthorizationException.class)
    public Object handleAuthorizationException(HttpServletRequest request, ServletResponse response) {
        if (HttpUtils.isAjaxRequest(request)) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            //http的状态码为403
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return ResponseBo.error("无权限！");
        } else {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("error/403");
            return mav;
        }
    }

    /**
     * 捕获其它的异常
     */
    @ExceptionHandler(value =Exception.class)
    public Object handleException(HttpServletRequest request, ServletResponse response,Exception e) {

        //打印日志
        log.error("全局异常捕获",e);

        if (HttpUtils.isAjaxRequest(request)) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            //http的状态码为500
            httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseBo.error("");
        } else {
            ModelAndView mav = new ModelAndView();
            mav.setViewName("error/500");
            return mav;
        }
    }

}
