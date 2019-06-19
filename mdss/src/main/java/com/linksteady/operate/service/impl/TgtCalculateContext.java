package com.linksteady.operate.service.impl;

import com.linksteady.operate.domain.TargetInfo;
import com.linksteady.operate.service.TargetListService;
import com.linksteady.operate.service.TgtCalculateService;
import com.linksteady.operate.vo.TgtReferenceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 目标进行计算的策略类
 */
@Component
@Slf4j
public class TgtCalculateContext {

    /**
     * 注入策略
     */
    @Autowired
    private  Map<String, TgtCalculateService> tgtCalculateServiceMap;

    private static final String TGT_KPI_CODE_GMV="gmv";

    private static final String TGT_KPI_CODE_UCNT="ucnt";

    @Autowired
    TargetListService targetListService;


    private TgtCalculateService getTgtCalculateService(String kpiCode)
    {
        if(TGT_KPI_CODE_GMV.equals(kpiCode))
        {
            return tgtCalculateServiceMap.get("tgtGmvCalculateServiceImpl");
        }else if(TGT_KPI_CODE_UCNT.equals(kpiCode))
        {
            return tgtCalculateServiceMap.get("tgtUserCalculateServiceImpl");
        }else
        {
            return null;
        }
    }
    /**
     * 获取参照信息
     * @param period
     * @param startDt
     * @param endDt
     * @param dimInfo
     * @return
     */
    public List<TgtReferenceVO> getReferenceData(String kpiCode,String period, String startDt, String endDt, Map<String,String> dimInfo)
    {

        TgtCalculateService tgtCalculateService=getTgtCalculateService(kpiCode);
         return tgtCalculateService.getReferenceData(period,startDt,endDt,dimInfo);
    }

    /**
     * 对某个目标的完成情况进行计算
     * @param targetInfo
     */
    public void calculateTarget(String kpiCode,TargetInfo targetInfo){
        TgtCalculateService tgtCalculateService=getTgtCalculateService(kpiCode);
        tgtCalculateService.calculateTarget(targetInfo);
    }

    /**
     * 对目标进行拆解
     * @param targetId
     */
    public void targetSplit(String kpiCode,Long targetId)
    {
        TgtCalculateService tgtCalculateService=getTgtCalculateService(kpiCode);
        try {
            tgtCalculateService.targetSplit(targetId);
        } catch (Exception e) {
            log.error("ID: {} 拆分计算任务异常",targetId,e);
            //更新目标的状态为错误状态
            targetListService.updateTargetStatus(targetId,"-1");
        }
    }
}

