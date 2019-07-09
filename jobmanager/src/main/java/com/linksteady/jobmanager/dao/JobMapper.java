package com.linksteady.jobmanager.dao;

import com.linksteady.jobmanager.config.MyMapper;
import com.linksteady.jobmanager.domain.Job;
import java.util.List;

public interface JobMapper extends MyMapper<Job> {

	List<Job> queryList();

	Long getJobSeqId();
}