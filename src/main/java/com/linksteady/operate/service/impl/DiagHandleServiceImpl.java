package com.linksteady.operate.service.impl;

import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.dao.CommonSelectMapper;
import com.linksteady.operate.domain.DiagHandleInfo;
import com.linksteady.operate.domain.DiagResultInfo;
import com.linksteady.operate.service.DiagHandleService;
import com.linksteady.operate.service.DiagOpService;
import com.linksteady.operate.util.UomsConstants;
import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    CommonSelectMapper commonSelectMapper;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Autowired
    DiagOpAddServiceImpl diagOpAddService;

    @Autowired
    DiagOpMultiServiceImpl diagOpMultiService;

    @Autowired
    DiagOpFilterServiceImpl diagOpFilterService;


    @Override
    public void saveResultToRedis(DiagResultInfo diagResultInfo) {
        redisTemplate.opsForValue().set("diagresult:"+diagResultInfo.getDiagId()+":"+diagResultInfo.getKpiLevelId(),diagResultInfo);
    }

    @Override
    public DiagResultInfo getResultFromRedis(int diagId, int kpiLevelId) {
        return (DiagResultInfo)redisTemplate.opsForValue().get("diagresult:"+diagId+":"+kpiLevelId);
    }


    @Override
    public DiagResultInfo generateDiagData(DiagHandleInfo diagHandleInfo) {
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

        return result;
    }
}
