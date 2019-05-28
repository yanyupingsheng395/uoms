package com.linksteady.operate.task;

import com.linksteady.operate.dao.TargetListMapper;
import com.linksteady.operate.domain.TargetInfo;
import com.linksteady.operate.service.impl.TgtGmvCalculateServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 对所有目标进行运算
 */
@Slf4j
@Component
public class CalculateAllTargetTask {

    @Autowired
    private TargetListMapper targetListMapper;

    @Autowired
    TgtGmvCalculateServiceImpl tgtGmvCalculateService;

    public void calculate()
    {
          log.info("开始批量计算任务的完成信息，开始的时间为:{}", LocalDate.now());
          //获取到所有需要计算的目标 (状态为 执行中)
          List<TargetInfo> list=targetListMapper.getAllRuningTarget();

          for(TargetInfo targetInfo:list)
          {
              if("gmv".equals(targetInfo.getKpiCode()))
              {
                  try {
                      tgtGmvCalculateService.calculateTarget(targetInfo);
                  } catch (Exception e) {
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
        log.info("结束批量计算任务的完成信息，结束的时间为:{}", LocalDate.now());
    }
}
