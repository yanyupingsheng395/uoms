package com.linksteady.mdss.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linksteady.common.annotation.Log;
import com.linksteady.common.domain.SysLog;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.mdss.service.LogService;
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
                criteria.andCondition("username=", sysLog.getUsername());
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
            log.error("????????????????????????", e);
            //???????????????????????????
            exceptionNoticeHandler.exceptionNotice(e);

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
            // ??????????????????
            String loganno = logAnnotation.value();
            loganno = loganno.length() > 1000 ? loganno.substring(0, 1000):loganno;
            log.setOperation(loganno);
        }
        // ???????????????
        String className = joinPoint.getTarget().getClass().getName();
        // ??????????????????
        String methodName = signature.getName();
        String methodParam = className + "." + methodName + "()";
        methodParam = methodParam.length() > 1000?methodParam.substring(0, 1000):methodParam;
        log.setMethod(methodParam);
        // ????????????????????????
        Object[] args = joinPoint.getArgs();
        // ???????????????????????????
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        if (args != null && paramNames != null) {
            StringBuilder params = new StringBuilder();
            params = handleParams(params, args, Arrays.asList(paramNames));
            params = params.length() > 1000?new StringBuilder(params.substring(0, 1000)):params;
            log.setParams(params.toString());
        }
        log.setCreateTime(new Date());
        // ??????????????????
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
                        // ???????????????NoSuchMethodException ??????????????? toString ?????? ????????????writeValueAsString ????????? ??? Object??? toString??????
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


