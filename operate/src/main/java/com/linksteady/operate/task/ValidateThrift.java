package com.linksteady.operate.task;

import com.linksteady.operate.thrift.RetentionData;
import com.linksteady.operate.thrift.ThriftClient;
import com.linksteady.smp.starter.annotation.JobHandler;
import com.linksteady.smp.starter.domain.ResultInfo;
import com.linksteady.smp.starter.jobclient.service.IJobHandler;
import com.linksteady.smp.starter.lognotice.service.ExceptionNoticeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 对thrift的运行状态进行监测
 * @author huang
 */
@Slf4j(topic = "jobLog")
@Component
@JobHandler(value = "validateThrift")
public class ValidateThrift extends IJobHandler {
    @Autowired
    private ThriftClient thriftClient;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    private ReentrantLock lock = new ReentrantLock();

    @Override
    public ResultInfo execute(String param) {

        lock.lock();
        try {
            if (!thriftClient.isOpend()) {
                thriftClient.open();
            }

            RetentionData retentionFitData = thriftClient.getInsightService().getRetentionFitData(-1, 0, 12);

            log.info("测试thrift接口，返回的结果为:{}",retentionFitData);
            return ResultInfo.success("测试thrift接口成功!");
        } catch (Exception e) {
            log.error("thrift接口获取拟合值数据异常", e);
            thriftClient.close();
            //错误日志的上报
            exceptionNoticeHandler.exceptionNotice(e.getMessage());
            return ResultInfo.faild(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

}


