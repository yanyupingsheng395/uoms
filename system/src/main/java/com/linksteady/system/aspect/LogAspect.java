package com.linksteady.system.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.domain.SysLog;
import com.linksteady.common.domain.User;
import com.linksteady.common.service.LogService;
import com.linksteady.common.util.HttpContextUtils;
import com.linksteady.common.util.IPUtils;
import com.linksteady.system.config.SystemProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * AOP 记录用户操作日志
 *
 * @author
 * @link
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private LogService logService;


    @Pointcut("@annotation(com.linksteady.common.annotation.Log)")
    public void pointcut() {
        // do nothing
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws JsonProcessingException {
        Object result = null;
        long beginTime = System.currentTimeMillis();
        try {
            // 执行方法
            result = point.proceed();
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
        // 执行时长(毫秒)
        // 获取request
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        // 设置IP地址
        String ip = IPUtils.getIpAddr(request);
        long time = System.currentTimeMillis() - beginTime;
        if (systemProperties.isOpenAopLog()) {
            // 保存日志
            UserBo userBo = (UserBo) SecurityUtils.getSubject().getPrincipal();
            SysLog log = new SysLog();
            log.setUsername(null == userBo ? "":userBo.getUsername());
            log.setIp(ip);
            log.setTime(time);
            logService.saveLog(point, log);
        }
        return result;
    }
}
