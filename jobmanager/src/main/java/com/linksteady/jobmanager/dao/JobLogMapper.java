package com.linksteady.jobmanager.dao;

import com.linksteady.jobmanager.config.MyMapper;
import com.linksteady.jobmanager.domain.JobLog;

public interface JobLogMapper extends MyMapper<JobLog> {

    Long getJobLogId();
}