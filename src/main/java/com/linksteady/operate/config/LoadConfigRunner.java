package com.linksteady.operate.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.CacheMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class LoadConfigRunner implements CommandLineRunner {

    @Autowired
    CacheMapper cacheMapper;

    @Override
    public void run(String... args) throws Exception {

        //所有诊断用到KPI
        List<Map<String,String>> kpis=cacheMapper.getDiagKpis();

        Map<String,String>  codeNamePair = Maps.newLinkedHashMap();
        Map<String,String>  codeFomularPair = Maps.newLinkedHashMap();

        Map<String,Object> kpidismant=Maps.newLinkedHashMap();

        for(Map<String,String> param:kpis)
        {
            codeNamePair.put(param.get("KPI_CODE"),param.get("KPI_NAME"));

            if("Y".equals(param.get("DISMANT_FLAG")))
            {
                codeFomularPair.put(param.get("KPI_CODE"),param.get("DISMANT_FORMULA"));

                //对于每一个kpi取获取其拆解信息
                String kpiCode=param.get("KPI_CODE");
                List<Map<String,String>>  dismantKpis=cacheMapper.getDismantKpis(kpiCode);
                if(null!=dismantKpis&&dismantKpis.size()>0)
                {
                    Map<String,String> temp=dismantKpis.get(0);
                    kpidismant.put(kpiCode,temp);
                }
            }
        }

        //获取诊断功能的维度列表
        List<Map<String,String>> diagDims=cacheMapper.getDiagDims();

        Map<String,String> DiagDimList=Maps.newLinkedHashMap();
        Map<String,Object> diagDimValueList=Maps.newLinkedHashMap();

        String dimCode="";
        String valueType="";
        String valueSql="";
        for(Map<String,String> diagDim:diagDims)
        {
            dimCode=diagDim.get("DIM_CODE");
            valueType=diagDim.get("VALUE_TYPE");
            valueSql=diagDim.get("VALUE_SQL");
            DiagDimList.put(dimCode,diagDim.get("DIM_NAME"));

            List<Map<String,Object>> dimValuesList=null;
            Map<String,String> dimValues=Maps.newHashMap();
            if("S".equals(valueType))  //通过sql查询
            {
                dimValuesList=cacheMapper.getDimValuesBySql(valueSql);

            }else //从数据库码表中取
            {
                dimValuesList=cacheMapper.getDimValuesDirect(dimCode);
            }

            for(Map<String,Object> param:dimValuesList)
            {
                dimValues.put(param.get("VALUE_CODE").toString(),param.get("VALUE_DESC").toString());
            }
            diagDimValueList.put(dimCode,dimValues);
        }

        //获取原因分析的维度列表
        List<Map<String,String>> reasonDims=cacheMapper.getReasonDims();

        Map<String,String> reasonDimList=Maps.newLinkedHashMap();
        Map<String,Object> reasonDimValueList=Maps.newLinkedHashMap();

        for(Map<String,String> reasonDim:reasonDims)
        {
            dimCode=reasonDim.get("DIM_CODE");
            valueType=reasonDim.get("VALUE_TYPE");
            valueSql=reasonDim.get("VALUE_SQL");
            reasonDimList.put(dimCode,reasonDim.get("DIM_NAME"));

            List<Map<String,Object>> dimValuesList2=null;
            Map<String,String> reasonDimValues=Maps.newHashMap();
            if("S".equals(valueType))  //通过sql查询
            {
                dimValuesList2=cacheMapper.getDimValuesBySql(valueSql);

            }else //从数据库码表中取
            {
                dimValuesList2=cacheMapper.getDimValuesDirect(dimCode);
            }

            for(Map<String,Object> param:dimValuesList2)
            {
                reasonDimValues.put(param.get("VALUE_CODE").toString(),param.get("VALUE_DESC").toString());
            }
            reasonDimValueList.put(dimCode,reasonDimValues);
        }


        //原因探究 相关指标 缓存
        List<Map<String,String>> reasonRelateKpis=cacheMapper.getReasonRelateKpis();

        Map<String, Object> reaonRelateKpi= Maps.newHashMap();
        Map<String,String> temp=null;
        for(Map<String,String> kpi:reasonRelateKpis)
        {
            temp=Maps.newHashMap();
            temp.put("REASON_KPI_NAME",kpi.get("REASON_KPI_NAME"));
            temp.put("TEMPLATE_CODE",kpi.get("TEMPLATE_CODE"));
            temp.put("TEMPLATE_NAME",kpi.get("TEMPLATE_NAME"));
            temp.put("REASON_KPI_ORDER",String.valueOf(kpi.get("REASON_KPI_ORDER")));
            temp.put("TEMPLATE_ORDER",String.valueOf(kpi.get("TEMPLATE_ORDER")));

            reaonRelateKpi.put(kpi.get("REASON_KPI_CODE"),temp);
        }

        KpiCacheManager.getInstance().setCacheMap(codeNamePair,"codeNamePair");
        KpiCacheManager.getInstance().setCacheMap(codeFomularPair,"codeFomularPair");
        KpiCacheManager.getInstance().setCacheMapForObject(kpidismant,"kpidismant");
        KpiCacheManager.getInstance().setCacheMap(DiagDimList,"diagDimList");

        KpiCacheManager.getInstance().setCacheMapForObject(diagDimValueList,"diagDimValueList");

        KpiCacheManager.getInstance().setCacheMap(reasonDimList,"reaonDimList");
        KpiCacheManager.getInstance().setCacheMapForObject(reasonDimValueList,"reasonDimValueList");

        KpiCacheManager.getInstance().setCacheMapForObject(reaonRelateKpi,"reasonRelateKpiList");


    }
}
