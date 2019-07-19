package com.linksteady.operate.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linksteady.common.annotation.Log;
import com.linksteady.common.domain.SysLog;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.operate.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

@Service("logService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
@Slf4j
public class LogServiceImpl extends BaseService<SysLog> implements LogService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Override
    public List<SysLog> findAllLogs(SysLog sysLog) {
        try {
            Example example = new Example(SysLog.class);
            Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(sysLog.getUsername())) {
                criteria.andCondition("username=", sysLog.getUsername().toLowerCase());
            }
            if (StringUtils.isNotBlank(sysLog.getOperation())) {
                criteria.andCondition("operation like", "%" + sysLog.getOperation() + "%");
            }
            if (StringUtils.isNotBlank(sysLog.getTimeField())) {
                String[] timeArr = sysLog.getTimeField().split("~");
                criteria.andCondition("to_char(create_time,'YYYYMMDD') >=", timeArr[0].replace("-",""));
                criteria.andCondition("to_char(create_time,'YYYYMMDD') <=", timeArr[1].replace("-",""));
            }
            example.setOrderByClause("create_time desc");
            return this.selectByExample(example);
        } catch (Exception e) {
            log.error("获取系统日志失败", e);
            //进行异常日志的上报
            exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));

            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void deleteLogs(String logIds) {
        List<String> list = Arrays.asList(logIds.split(","));
        this.batchDelete(list, "id", SysLog.class);
    }

    @Override
    public void saveLog(ProceedingJoinPoint joinPoint, SysLog log) throws JsonProcessingException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log logAnnotation = method.getAnnotation(Log.class);
        if (logAnnotation != null) {
            // 注解上的描述
            String loganno = logAnnotation.value();
            loganno = loganno.length() > 1000 ? loganno.substring(0, 1000):loganno;
            log.setOperation(loganno);
        }
        // 请求的类名
        String className = joinPoint.getTarget().getClass().getName();
        // 请求的方法名
        String methodName = signature.getName();
        String methodParam = className + "." + methodName + "()";
        methodParam = methodParam.length() > 1000?methodParam.substring(0, 1000):methodParam;
        log.setMethod(methodParam);
        // 请求的方法参数值
        Object[] args = joinPoint.getArgs();
        // 请求的方法参数名称
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        if (args != null && paramNames != null) {
            StringBuilder params = new StringBuilder();
            params = handleParams(params, args, Arrays.asList(paramNames));
            params = params.length() > 1000?new StringBuilder(params.substring(0, 1000)):params;
            log.setParams(params.toString());
        }
        log.setCreateTime(new Date());
        // 保存系统日志
        save(log);
    }

    @SuppressWarnings("unchecked")
    private StringBuilder handleParams(StringBuilder params, Object[] args, List paramNames) throws JsonProcessingException {
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Map) {
                Set set = ((Map) args[i]).keySet();
                List list = new ArrayList();
                List paramList = new ArrayList<>();
                for (Object key : set) {
                    list.add(((Map) args[i]).get(key));
                    paramList.add(key);
                }
                return handleParams(params, list.toArray(), paramList);
            } else {
                if (args[i] instanceof Serializable) {
                    Class<?> aClass = args[i].getClass();
                    try {
                        aClass.getDeclaredMethod("toString", new Class[]{null});
                        // 如果不抛出NoSuchMethodException 异常则存在 toString 方法 ，安全的writeValueAsString ，否则 走 Object的 toString方法
                        params.append("  ").append(paramNames.get(i)).append(": ").append(objectMapper.writeValueAsString(args[i]));
                    } catch (NoSuchMethodException e) {
                        params.append("  ").append(paramNames.get(i)).append(": ").append(objectMapper.writeValueAsString(args[i].toString()));
                    }
                } else if (args[i] instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) args[i];
                    params.append("  ").append(paramNames.get(i)).append(": ").append(file.getName());
                } else {
                    params.append("  ").append(paramNames.get(i)).append(": ").append(args[i]);
                }
            }
        }
        return params;
    }
}


