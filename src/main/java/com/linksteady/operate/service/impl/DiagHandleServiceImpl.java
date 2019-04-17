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
    public DiagResultInfo generateDiagData(DiagHandleInfo diagHandleInfo) {
        //获取到操作类型
        String handleType=diagHandleInfo.getHandleType();

        if("M".equals(handleType)) //乘法
        {

        }else if("A".equals(handleType))  //加法
        {

        }else if("F".equals(handleType))  //过滤
        {

        }
        return new DiagResultInfo();
    }
}
