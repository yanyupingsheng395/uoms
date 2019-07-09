package com.linksteady.jobmanager.service;

import com.linksteady.jobmanager.domain.Job;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@CacheConfig(cacheNames = "JobService")
public interface JobService extends IService<Job> {

    Job findJob(Long jobId);

    List<Job> findAllJobs(Job job);

    void addJob(Job job);

    void updateJob(Job job) throws Exception;

    void deleteBatch(String jobIds);

    int updateBatch(String jobIds, String status);

    void run(String jobIds);

    void pause(String jobIds);

    void resume(String jobIds);

    @Cacheable(key = "#p0")
    List<Job> getSysCronClazz(Job job);
}
