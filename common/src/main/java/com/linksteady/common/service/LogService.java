package com.linksteady.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.linksteady.common.domain.SysLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface LogService extends IService<SysLog> {
	
	List<SysLog> findAllLogs(SysLog sysLog);
	
	void deleteLogs(String logIds);

	@Async
	void saveLog(ProceedingJoinPoint point, SysLog sysLog) throws JsonProcessingException;

	@Async
	void saveLog(SysLog sysLog);
}
