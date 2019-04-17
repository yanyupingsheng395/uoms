package com.linksteady.operate.service.impl;

import com.linksteady.operate.domain.DiagHandleInfo;
import com.linksteady.operate.domain.DiagResultInfo;
import com.linksteady.operate.service.DiagHandleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class DiagHandleServiceImpl implements DiagHandleService {

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void saveHandleInfoToRedis(DiagHandleInfo diagHandleInfo) {
        redisTemplate.opsForValue().set("diag:"+diagHandleInfo.getDiagId()+":"+diagHandleInfo.getKpiLevelId(),diagHandleInfo);
    }

    @Override
    public DiagHandleInfo getHandleInfoFromRedis(int diagId, int kpiLevelId) {
        return (DiagHandleInfo)redisTemplate.opsForValue().get("diag:"+diagId+":"+kpiLevelId);
    }

    @Override
    public void saveResultToRedis(DiagResultInfo diagResultInfo) {
        redisTemplate.opsForValue().set("diagresult:"+diagResultInfo.getDiagId()+":"+diagResultInfo.getKpiLevelId(),diagResultInfo);
    }

    @Override
    public DiagResultInfo getResultFromRedis(int diagId, int kpiLevelId) {
        return (DiagResultInfo)redisTemplate.opsForValue().get("diag:"+diagId+":"+kpiLevelId);
    }


    @Override
    public DiagResultInfo generateDiagData(DiagHandleInfo diagHandleInfo) {
        //获取到操作类型
        String handleType=diagHandleInfo.getHandleType();
        DiagResultInfo result=null;

        if("M".equals(handleType)) //乘法
        {
             result=processMultiple(diagHandleInfo);
        }else if("A".equals(handleType))  //加法
        {
             result=processAdd(diagHandleInfo);
        }else if("F".equals(handleType))  //过滤
        {
            result=processFilter(diagHandleInfo);
        }

        result.setDiagId(diagHandleInfo.getDiagId());
        result.setKpiLevelId(diagHandleInfo.getKpiLevelId());
        result.setBeginDt(diagHandleInfo.getBeginDt());
        result.setEndDt(diagHandleInfo.getEndDt());
        result.setPeriodType(diagHandleInfo.getPeriodType());
        result.setHandleDesc(diagHandleInfo.getHandleDesc());

        //增加条件信息
        result.setWhereinfo(diagHandleInfo.getWhereinfo()); //todo 考虑对map重构，仅返回必要信息，减少数据传输量

        //result信息持久化到redis
        //saveResultToRedis(result);

        return result;
    }


    /**
     * 处理乘法运算
     * @param diagHandleInfo
     * @return
     */
    private DiagResultInfo processMultiple(DiagHandleInfo diagHandleInfo)
    {
        //概览信息

        //获取三个指标的折线图

        //末期比基期的变化率；

        //均值及上下5%区域；

        //变异系数表格

        return new DiagResultInfo();
    }

    /**
     * 处理加法运算
     * @param diagHandleInfo
     * @return
     */
    private DiagResultInfo processAdd(DiagHandleInfo diagHandleInfo)
    {
        //概览信息

        //核心指标  面积图

        //非核心指标 其它指标各部分的趋势图与均线;

        return new DiagResultInfo();
    }

    /**
     * 处理过滤运算
     * @param diagHandleInfo
     * @return
     */
    private DiagResultInfo processFilter(DiagHandleInfo diagHandleInfo)
    {
        //概览信息

        //操作描述

        //条件信息

        return new DiagResultInfo();
    }
}
