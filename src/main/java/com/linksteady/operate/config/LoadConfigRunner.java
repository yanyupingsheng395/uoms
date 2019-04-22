package com.linksteady.operate.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.CacheMapper;
import com.linksteady.operate.domain.KpiConfigInfo;
import com.linksteady.operate.domain.KpiDismantInfo;
import com.linksteady.operate.domain.ReasonTemplateInfo;
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
        List<KpiConfigInfo> kpis=cacheMapper.getKpiList();

        //诊断指标列表
        Map<String,String>  diagKpiList = Maps.newLinkedHashMap();
        //原因探究指标列表
        Map<String,String>  reasonKpiList=Maps.newLinkedHashMap();
        //诊断公式列表
        Map<String,String>  DiagcodeFomularList = Maps.newLinkedHashMap();
        //诊断乘法拆解方式
        Map<String, KpiDismantInfo> kpidismant=Maps.newLinkedHashMap();


        for(KpiConfigInfo kpiConfigInfo:kpis)
        {
            //诊断指标列表
            if("Y".equals(kpiConfigInfo.getDiagFlag()))
            {
                diagKpiList.put(kpiConfigInfo.getKpiCode(),kpiConfigInfo.getKpiName());

                //是否可拆解的标志
                if("Y".equals(kpiConfigInfo.getDismantFlag()))
                {
                    DiagcodeFomularList.put(kpiConfigInfo.getKpiCode(),kpiConfigInfo.getDismantFormula());

                    //对于每一个kpi取获取其拆解信息
                    List<KpiDismantInfo>  dismantInfoList=cacheMapper.getDismantKpis(kpiConfigInfo.getKpiCode());

                    for(KpiDismantInfo kpiDismantInfo:dismantInfoList)
                    {
                        kpidismant.put(kpiConfigInfo.getKpiCode(),kpiDismantInfo);
                    }
                }
            }

            //原因探究指标列表
            if("Y".equals(kpiConfigInfo.getReasonFlag()))
            {
                reasonKpiList.put(kpiConfigInfo.getKpiCode(),kpiConfigInfo.getKpiName());
            }
        }

        //获取诊断功能的维度列表
        List<Map<String,String>> diagDims=cacheMapper.getDiagDims();
        Map<String,String> DiagDimList=Maps.newLinkedHashMap();
        Map<String,Map<String,String>> diagDimValueList=Maps.newLinkedHashMap();

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
        Map<String,Map<String,String>> reasonDimValueList=Maps.newLinkedHashMap();

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
        List<ReasonTemplateInfo> reasonRelateKpis=cacheMapper.getReasonRelateKpis();

        Map<String, ReasonTemplateInfo> reaonRelateKpi= Maps.newHashMap();
        for(ReasonTemplateInfo kpi:reasonRelateKpis)
        {
            reaonRelateKpi.put(kpi.getReasonKpiCode(),kpi);
        }

        KpiCacheManager.getInstance().setCacheMap(diagKpiList,"diagKpiList");
        KpiCacheManager.getInstance().setCacheMap(DiagcodeFomularList,"DiagcodeFomularList");
        KpiCacheManager.getInstance().setCacheForKpiDismant(kpidismant);
        KpiCacheManager.getInstance().setCacheMap(DiagDimList,"diagDimList");

        KpiCacheManager.getInstance().setCacheMapForCommonMmap(diagDimValueList,"diagDimValueList");

        KpiCacheManager.getInstance().setCacheMap(reasonKpiList,"reasonKpiList");
        KpiCacheManager.getInstance().setCacheMap(reasonDimList,"reaonDimList");
        KpiCacheManager.getInstance().setCacheMapForCommonMmap(reasonDimValueList,"reasonDimValueList");

        KpiCacheManager.getInstance().setCacheForReasonTemplate(reaonRelateKpi);


    }
}
