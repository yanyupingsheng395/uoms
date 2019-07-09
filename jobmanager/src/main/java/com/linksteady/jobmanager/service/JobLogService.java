package com.linksteady.jobmanager.service;

import com.linksteady.jobmanager.domain.JobLog;

import java.util.List;

public interface JobLogService extends IService<JobLog>{

	List<JobLog> findAllJobLogs(JobLog jobLog);

	void saveJobLog(JobLog log);
	
	void deleteBatch(String jobLogIds);
}
