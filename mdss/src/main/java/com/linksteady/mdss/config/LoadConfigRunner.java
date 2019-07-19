package com.linksteady.mdss.config;

import com.linksteady.mdss.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 将需要加载到内存中的数据加载到内存中
 * @author huang
 */
@Component
public class LoadConfigRunner implements CommandLineRunner {

    @Autowired
    CacheService cacheService;

    @Override
    public void run(String... args) throws Exception {
        cacheService.procKpiLoad();
        cacheService.procDiagDimLoad();
        cacheService.procReasonDimLoad();
        cacheService.procReasonRelateKpiLoad();

        cacheService.procKpiSqlTemplate();
        cacheService.procDimListLoad();
        cacheService.procDimJoinInfoLoad();
    }
}
