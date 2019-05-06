package com.linksteady.operate.config;

import com.google.common.base.Splitter;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.linksteady.operate.dao.CacheMapper;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.service.CacheService;
import com.linksteady.operate.vo.DimJoinVO;
import com.linksteady.operate.vo.KpiSqlTemplateVO;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
