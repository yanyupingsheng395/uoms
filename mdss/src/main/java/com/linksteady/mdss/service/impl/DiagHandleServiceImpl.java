package com.linksteady.mdss.service.impl;

import com.linksteady.mdss.config.KpiCacheManager;
import com.linksteady.mdss.domain.DiagHandleInfo;
import com.linksteady.mdss.domain.DiagResultInfo;
import com.linksteady.mdss.service.DiagHandleService;
import com.linksteady.mdss.util.UomsConstants;
import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 诊断-数据处理
 * @author huang
 */
@Service
@Slf4j
public class DiagHandleServiceImpl implements DiagHandleService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Autowired
    DiagOpAddServiceImpl diagOpAddService;

    @Autowired
    DiagOpMultiServiceImpl diagOpMultiService;

    @Autowired
    DiagOpFilterServiceImpl diagOpFilterService;


    /**
     * 将diagResultInfo存储到redis中
     * @param diagResultInfo
     */
    @Override
    public void saveResultToRedis(DiagResultInfo diagResultInfo) {
        redisTemplate.opsForValue().set("diagresult:"+diagResultInfo.getDiagId()+":"+diagResultInfo.getKpiLevelId(),diagResultInfo);
    }

    /**
     * 从redis中获取DiagResultInfo对象
     * @param diagId
     * @param kpiLevelId
     * @return
     */
    @Override
    public DiagResultInfo getResultFromRedis(int diagId, int kpiLevelId) {
        return (DiagResultInfo)redisTemplate.opsForValue().get("diagresult:"+diagId+":"+kpiLevelId);
    }

    /**
     * 从redis中获取DiagHandleInfo对象
     * @param diagId
     * @param kpiLevelId
     * @return
     */
    @Override
    public DiagHandleInfo getDiagHandleInfoFromRedis(int diagId, int kpiLevelId) {
        return (DiagHandleInfo)redisTemplate.opsForValue().get("diagHandleInfo:"+diagId+":"+kpiLevelId);
    }

    /**
     * 将diagHandleInfo对象存储到redis中
     * @param diagHandleInfo
     */
    @Override
    public void setDiagHandleInfoToRedis(DiagHandleInfo diagHandleInfo) {
        redisTemplate.opsForValue().set("diagHandleInfo:"+diagHandleInfo.getDiagId()+":"+diagHandleInfo.getKpiLevelId(),diagHandleInfo);
    }

    /**
     * 通过diagHandleInfo 获取图表拆解后数据，并存储到redis中
     * 如果是跟节点，handleType为F，和过滤条件的处理方式一样
     * @param diagHandleInfo
     * @return
     */
    @Override
    public DiagResultInfo generateDiagData(DiagHandleInfo diagHandleInfo) {

        long beginTime = System.currentTimeMillis();
        //获取到操作类型
        String handleType=diagHandleInfo.getHandleType();
        DiagResultInfo result=null;

        //乘法
        if(UomsConstants.DIAG_OPERATION_TYPE_MULTI.equals(handleType))
        {
             result=diagOpMultiService.process(diagHandleInfo);
        }else if(UomsConstants.DIAG_OPERATION_TYPE_ADD.equals(handleType))
        {   //加法
             result=diagOpAddService.process(diagHandleInfo);
        }else if(UomsConstants.DIAG_OPERATION_TYPE_FILTER.equals(handleType))
        {   //过滤
            result=diagOpFilterService.process(diagHandleInfo);
        }

        result.setDiagId(diagHandleInfo.getDiagId());
        result.setKpiLevelId(diagHandleInfo.getKpiLevelId());
        result.setBeginDt(diagHandleInfo.getBeginDt());
        result.setEndDt(diagHandleInfo.getEndDt());
        result.setPeriodType(diagHandleInfo.getPeriodType());
        result.setHandleDesc(diagHandleInfo.getHandleDesc());
        //操作类型
        result.setHandleType(handleType);

        //指标编码
        result.setKpiCode(diagHandleInfo.getKpiCode());
        //指标名称
        result.setKpiName(KpiCacheManager.getInstance().getDiagKpiList().get(diagHandleInfo.getKpiCode()).getKpiName());
        //过滤条件
        result.setWhereinfo(diagHandleInfo.getWhereinfo());

        //result信息持久化到redis
        saveResultToRedis(result);

        long time = System.currentTimeMillis() - beginTime;

        if(log.isDebugEnabled())
        {
            log.debug("诊断ID={},LEVEL_ID={},获取数据消耗的时间长度为{}",diagHandleInfo.getDiagId(),diagHandleInfo.getKpiLevelId(),time);
        }
        return result;
    }
}
