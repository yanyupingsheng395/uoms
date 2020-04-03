package com.linksteady.operate.task;

import com.linksteady.jobclient.annotation.JobHandler;
import com.linksteady.jobclient.domain.ResultInfo;
import com.linksteady.jobclient.service.IJobHandler;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.thrift.InsightThriftClient;
import com.linksteady.operate.thrift.RetentionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 对thrift的运行状态进行监测
 * @author huang
 */
@Slf4j(topic = "jobLog")
@Component
@JobHandler(value = "validateThrift")
public class ValidateThrift extends IJobHandler {
    @Autowired
    private InsightThriftClient insightThriftClient;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    private ReentrantLock lock = new ReentrantLock();

    @Override
    public ResultInfo execute(String param) {

        lock.lock();
        try {
            if (!insightThriftClient.isOpend()) {
                insightThriftClient.open();
            }

            RetentionData retentionFitData = insightThriftClient.getInsightService().getRetentionFitData(-1, 0, 12);

            log.info("测试thrift接口，返回的结果为:{}",retentionFitData);
            return ResultInfo.success("测试thrift接口成功!");
        } catch (Exception e) {
            log.error("thrift接口获取拟合值数据异常", e);
            insightThriftClient.close();
            //错误日志的上报
            exceptionNoticeHandler.exceptionNotice(e.getMessage());
            return ResultInfo.faild(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

}


