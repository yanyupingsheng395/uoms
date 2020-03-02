package com.linksteady.operate.task;

import com.linksteady.jobclient.annotation.JobHandler;
import com.linksteady.jobclient.domain.ResultInfo;
import com.linksteady.jobclient.service.IJobHandler;
import com.linksteady.operate.dao.*;
import com.linksteady.operate.domain.ExecSteps;
import com.linksteady.operate.domain.enums.ActivityGroupEnum;
import com.linksteady.operate.exception.LinkSteadyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 对推送效果进行计算
 * @author huang
 */
@Slf4j
@Component
@JobHandler(value = "opDailyCalculate")
public class OpDailyCalculate extends IJobHandler {

    @Autowired
    private ExecStepsMapper execStepsMapper;

    @Override
    public ResultInfo execute(String param) {
        log.info("开始运营效果的计算，开始的时间为:{}", LocalDateTime.now());
        log.info("接收到的参数值为:{}",param);

        try {
            //判断参数类型
            if(StringUtils.isEmpty(param))
            {
                //每日运营
                executeSteps(EffectExecType.EFFECT_DAILY_KEY.getKey());
                //活动运营
                executeSteps(EffectExecType.EFFECT_ACTIVITY_KEY.getKey());
                //手工推送
                executeSteps(EffectExecType.EFFECT_MANUAL_KEY.getKey());

            }else if(EffectExecType.EFFECT_DAILY_KEY.getKey().equals(param))
            {
                //每日运营
                executeSteps(EffectExecType.EFFECT_DAILY_KEY.getKey());

            }else if(EffectExecType.EFFECT_ACTIVITY_KEY.getKey().equals(param))
            {
                //活动运营
                executeSteps(EffectExecType.EFFECT_ACTIVITY_KEY.getKey());

            }else if(EffectExecType.EFFECT_MANUAL_KEY.getKey().equals(param))
            {
                //手工推送
                executeSteps(EffectExecType.EFFECT_MANUAL_KEY.getKey());
            }else {
                throw new LinkSteadyException("无效的任务参数");
            }

            log.info("完成运营效果的计算，完成的时间为:{}", LocalDateTime.now());
            return ResultInfo.success("执行任务成功!");
        } catch (Exception e) {
            log.error("执行任务失败，失败的原因:{}",e);
            return ResultInfo.faild(e.getMessage());
        }

    }

    /**
     * 根据传入的业务标记获取到所有执行步骤并执行
     * @param keyName
     * @return
     */
    private void executeSteps(String keyName) throws Exception{
        List<ExecSteps> execStepsList=execStepsMapper.selctStepList(keyName);

        log.info("###############开始计算 {} ,合计步骤: ###############",EffectExecType.EFFECT_ACTIVITY_KEY.getDescByKey(keyName),execStepsList.size());

        ExecSteps execSteps=null;
        //遍历
        for(int i=0;i<=execStepsList.size();i++)
        {
            try{
                execSteps=execStepsList.get(i);

                log.info("    ####开始执行第{}步，名称为{},类型为{},开始时间:{}",i,execSteps.getStepName(),execSteps.getStepType(),LocalDateTime.now());

                //SQL类型
                if("SQL".equals(execSteps.getStepType()))
                {
                    if("DELETE".equals(execSteps.getSqlType()))
                    {
                        execStepsMapper.execCommonDeleteSqls(execSteps.getSqlContent());
                    }else  if("UPDATE".equals(execSteps.getSqlType()))
                    {
                        execStepsMapper.execCommonUpdateSqls(execSteps.getSqlContent());
                    }else  if("INSERT".equals(execSteps.getSqlType()))
                    {
                        execStepsMapper.execCommonInsertSqls(execSteps.getSqlContent());
                    }else
                    {
                        //抛出异常
                        throw new LinkSteadyException("无效的SQL类型");
                    }
                }else if("BEAN".equals(execSteps.getStepType()))
                {
                    //todo 后续完善
                }else
                {
                    throw new LinkSteadyException("无效的步骤类型");
                }
                log.info("    ####执行第{}步成功,完成时间:{}",i,LocalDateTime.now());
            }catch (Exception e)
            {
                log.error("    ####执行第{}步失败，异常堆栈为{}",e);
                throw e;
            }

        }
        log.info("###############计算 {}完成 ，完成的时间为{}",LocalDateTime.now());

    }
}

enum EffectExecType{
    EFFECT_DAILY_KEY("daily","每日运营效果计算"),
    EFFECT_ACTIVITY_KEY("activity","活动运营效果计算"),
    EFFECT_MANUAL_KEY("manual","活动运营效果计算");

    private String key;
    private String desc;

    EffectExecType(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDescByKey(String key)
    {
        for (EffectExecType c : EffectExecType.values()) {
            if (c.getKey().equals(key)) {
                return c.getDesc();
            }
        }
        return "";
    }
}


