package com.linksteady.jobmanager.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.linksteady.common.util.SpringContextUtils;
import com.linksteady.jobmanager.domain.Job;
import com.linksteady.jobmanager.domain.JobLog;
import com.linksteady.jobmanager.service.JobLogService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.concurrent.*;

/**
 * 定时任务
 *
 * @author MrBird
 */
public class ScheduleJob extends QuartzJobBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("thread-call-runner-%d").build();
    ExecutorService service = new ThreadPoolExecutor(2, 16, 60L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(16), threadFactory);

    @Override
    protected void executeInternal(JobExecutionContext context) {
        Job scheduleJob = (Job) context.getMergedJobDataMap().get(Job.JOB_PARAM_KEY);

        // 获取spring bean
        JobLogService scheduleJobLogService = (JobLogService) SpringContextUtils.getBean("JobLogService");

        JobLog log = new JobLog();
        log.setJobId(scheduleJob.getJobId());
        log.setBeanName(scheduleJob.getBeanName());
        log.setMethodName(scheduleJob.getMethodName());
        log.setParams(scheduleJob.getParams());
        log.setCreateTime(new Date());

        long startTime = System.currentTimeMillis();

        try {
            // 执行任务
            logger.info("任务准备执行，任务ID：{}, 当前线程：{}", scheduleJob.getJobId(), Thread.currentThread().getName());
            ScheduleRunnable task = new ScheduleRunnable(scheduleJob.getBeanName(), scheduleJob.getMethodName(),
                    scheduleJob.getParams());
            Future<?> future = service.submit(task);
            future.get();
            long times = System.currentTimeMillis() - startTime;
            log.setTimes(times);
            // 任务状态 0：成功 1：失败
            log.setStatus("0");

            logger.info("任务执行完毕，任务ID：{} 总共耗时：{} 毫秒, 当前线程：{}", scheduleJob.getJobId(), times, Thread.currentThread().getName());
        } catch (Exception e) {
            logger.error("任务执行失败，任务ID：" + scheduleJob.getJobId(), e);
            long times = System.currentTimeMillis() - startTime;
            log.setTimes(times);
            // 任务状态 0：成功 1：失败
            log.setStatus("1");
            log.setError(StringUtils.substring(e.toString(), 0, 2000));
        } finally {
            scheduleJobLogService.saveJobLog(log);
        }
    }
}