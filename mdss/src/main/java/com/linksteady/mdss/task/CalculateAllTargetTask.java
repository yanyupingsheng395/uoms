package com.linksteady.mdss.task;

import com.linksteady.jobclient.annotation.JobHandler;
import com.linksteady.jobclient.domain.ResultInfo;
import com.linksteady.jobclient.service.IJobHandler;
import com.linksteady.lognotice.service.ExceptionNoticeHandler;
import com.linksteady.mdss.dao.TargetListMapper;
import com.linksteady.mdss.domain.TargetInfo;
import com.linksteady.mdss.service.impl.TgtGmvCalculateServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 对所有目标进行运算
 * @author huang
 */
@Slf4j
@Component
@JobHandler(value = "calculateAllTargetTask")
public class CalculateAllTargetTask extends IJobHandler {

    @Autowired
    private TargetListMapper targetListMapper;

    @Autowired
    TgtGmvCalculateServiceImpl tgtGmvCalculateService;

    @Autowired
    ExceptionNoticeHandler exceptionNoticeHandler;

    @Override
    public ResultInfo execute(String param) throws Exception {
        log.info("开始批量计算任务的完成信息，开始的时间为:{}, 线程名称：{}", LocalDate.now(), Thread.currentThread().getName());
        // 获取到所有需要计算的目标 (状态为 执行中)
        List<TargetInfo> list=targetListMapper.getAllRuningTarget();

        for(TargetInfo targetInfo:list)
        {
            if("gmv".equals(targetInfo.getKpiCode()))
            {
                try {
                    tgtGmvCalculateService.calculateTarget(targetInfo);
                } catch (Exception e) {
                    //进行异常日志的上报
                    exceptionNoticeHandler.exceptionNotice(StringUtils.substring(ExceptionUtils.getStackTrace(e),1,512));
                    //更新任务的状态为失败
                    targetListMapper.updateTargetStatus(targetInfo.getId(),"-1");

                    //todo 写入预警表
                    log.info("批量计算ID为{}的任务失败",targetInfo.getId(),e);

                }
            }else
            {
                log.error("尚未配置此指标的计算");
            }
        }
        log.info("结束批量计算任务的完成信息，结束的时间为:{}, 线程名称：{}", LocalDate.now(), Thread.currentThread().getName());
        return ResultInfo.success("success");
    }
}
