package com.linksteady.operate.task;

import com.linksteady.operate.dao.ExecStepsMapper;
import com.linksteady.operate.domain.ExecSteps;
import com.linksteady.operate.exception.LinkSteadyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class CommonExecutors {

    @Autowired
    private ExecStepsMapper execStepsMapper;

    /**
     * 根据传入的业务标记获取到所有执行步骤并执行
     * @param keyName
     * @return
     */
    public void executeSteps(String keyName) throws Exception{
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<ExecSteps> execStepsList=execStepsMapper.selctStepList(keyName);

        log.info("###############开始计算 {} ,合计步骤:{} 步 ###############",ExecType.EFFECT_ACTIVITY_KEY.getDescByKey(keyName),execStepsList.size());

        ExecSteps execSteps=null;
        //遍历
        for(int i=0;i<execStepsList.size();i++)
        {
            try{
                execSteps=execStepsList.get(i);

                log.info("    ####开始执行第{}步，名称为{},类型为{},开始时间:{}",i,execSteps.getStepName(),execSteps.getStepType(),dtf2.format(LocalDateTime.now()));

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
                log.info("    ####成功执行第{}步,完成时间:{}",i,dtf2.format(LocalDateTime.now()));
            }catch (Exception e)
            {
                log.error("    ####失败执行第{}步，异常堆栈为{}",e);
                throw e;
            }

        }

        log.info("###############完成计算 {} ,完成的时间为{} ###############",ExecType.EFFECT_ACTIVITY_KEY.getDescByKey(keyName),dtf2.format(LocalDateTime.now()));

    }
}
