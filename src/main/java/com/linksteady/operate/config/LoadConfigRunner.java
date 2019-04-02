package com.linksteady.operate.config;

import com.google.common.collect.Maps;
import com.linksteady.operate.dao.KpiCacheMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LoadConfigRunner implements CommandLineRunner {

    @Autowired
    KpiCacheMapper kpiCacheMapper;

    @Override
    public void run(String... args) throws Exception {

        List<Map<String,String>> kpis=kpiCacheMapper.getDiagKpis();

        Map<String,String>  codeNamePair = Maps.newHashMap();
        Map<String,String>  codeFomulaPair = Maps.newHashMap();

        Map<String,Object> kpidismant=Maps.newHashMap();


        for(Map<String,String> param:kpis)
        {
            codeNamePair.put(param.get("KPI_CODE"),param.get("KPI_NAME"));
            codeFomulaPair.put(param.get("KPI_CODE"),param.get("DISMANT_FORMULA"));

            //对于每一个kpi取获取其拆解信息
            String kpiCode=param.get("KPI_CODE");
            List<Map<String,String>>  dismantKpis=kpiCacheMapper.getDismantKpis(kpiCode);
            if(null!=dismantKpis&&dismantKpis.size()>0)
            {
                 Map<String,String> temp=dismantKpis.get(0);
                 kpidismant.put(kpiCode,temp);
            }

        }
        KpiCacheManager.getInstance().setCodeNamePair(codeNamePair);
        KpiCacheManager.getInstance().setCodeFomularPair(codeFomulaPair);
        KpiCacheManager.getInstance().setKpiDismant(kpidismant);


    }
}
