package com.linksteady.operate.config;

import com.google.common.base.Splitter;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.linksteady.operate.dao.CacheMapper;
import com.linksteady.operate.domain.*;
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
    CacheMapper cacheMapper;

    @Autowired
    DozerBeanMapper dozerBeanMapper;

    @Override
    public void run(String... args) throws Exception {
         procKpiLoad();
         procDiagDimLoad();
         procReasonDimLoad();
         procReasonRelateKpiLoad();

         procKpiSqlTemplate();
         procDimListLoad();
         ProcDimJoinInfoLoad();
    }

    /**
     * 和指标相关的信息加载到缓存中
     */
    private void procKpiLoad()
    {
        //所有诊断用到KPI
        List<KpiConfigInfo> kpis=cacheMapper.getKpiList();

        //诊断指标列表
        Map<String,String>  diagKpiList = Maps.newLinkedHashMap();
        //原因探究指标列表
        Map<String,String>  reasonKpiList=Maps.newLinkedHashMap();
        //诊断公式列表
        Map<String,String> diagcodeFomularList = Maps.newLinkedHashMap();
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
                    diagcodeFomularList.put(kpiConfigInfo.getKpiCode(),kpiConfigInfo.getDismantFormula());

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

        KpiCacheManager.getInstance().setCacheMap(diagKpiList,"diagKpiList");
        KpiCacheManager.getInstance().setCacheMap(diagcodeFomularList,"DiagcodeFomularList");
        KpiCacheManager.getInstance().setCacheForKpiDismant(kpidismant);
        KpiCacheManager.getInstance().setCacheMap(reasonKpiList,"reasonKpiList");
    }

    /**
     * 维度、维度值相关数据加载到缓存中
     */
    private  void procDiagDimLoad()
    {
        //获取诊断功能的维度列表
        List<DimConfigInfo> diagDims=cacheMapper.getAllDimConfig().stream().filter(a->"Y".equals(a.getDiagFlag())).collect(Collectors.toList());

        Map<String,String> diagDimList=Maps.newLinkedHashMap();
        Map<String,Map<String,String>> diagDimValueList=Maps.newLinkedHashMap();

        String dimCode="";
        String valueType="";
        String valueSql="";
        for(DimConfigInfo diagDim:diagDims)
        {
            dimCode=diagDim.getDimCode();
            valueType=diagDim.getValueType();
            valueSql=diagDim.getValueSql();
            diagDimList.put(dimCode,diagDim.getDimName());

            List<Map<String,Object>> dimValuesList=null;
            Map<String,String> dimValues=Maps.newHashMap();
            //通过sql查询
            if("S".equals(valueType))
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

        KpiCacheManager.getInstance().setCacheMap(diagDimList,"diagDimList");

        KpiCacheManager.getInstance().setCacheMapForCommonMmap(diagDimValueList,"diagDimValueList");
    }

    /**
     * 原因相关的维度、维度值加载到缓存中
     */
    private void procReasonDimLoad()
    {
        //获取原因分析的维度列表
        List<DimConfigInfo> reasonDims=cacheMapper.getAllDimConfig().stream().filter(a->"Y".equals(a.getReasonFlag())).collect(Collectors.toList());

        Map<String,String> reasonDimList=Maps.newLinkedHashMap();
        Map<String,Map<String,String>> reasonDimValueList=Maps.newLinkedHashMap();

        String dimCode="";
        String valueType="";
        String valueSql="";
        for(DimConfigInfo reasonDim:reasonDims)
        {
            dimCode=reasonDim.getDimCode();
            valueType=reasonDim.getValueType();
            valueSql=reasonDim.getValueSql();
            reasonDimList.put(dimCode,reasonDim.getDimName());

            List<Map<String,Object>> dimValuesList2=null;

            Map<String,String> reasonDimValues=Maps.newHashMap();
            //通过sql查询
            if("S".equals(valueType))
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
        KpiCacheManager.getInstance().setCacheMap(reasonDimList,"reaonDimList");
        KpiCacheManager.getInstance().setCacheMapForCommonMmap(reasonDimValueList,"reasonDimValueList");

    }

    /**
     * 原因探究-原因指标加载
     */
    private void procReasonRelateKpiLoad()
    {
        //原因探究 相关指标 缓存
        List<ReasonTemplateInfo> reasonRelateKpis=cacheMapper.getReasonRelateKpis();

        Map<String, ReasonTemplateInfo> reaonRelateKpi= Maps.newHashMap();
        for(ReasonTemplateInfo kpi:reasonRelateKpis)
        {
            reaonRelateKpi.put(kpi.getReasonKpiCode(),kpi);
        }
        KpiCacheManager.getInstance().setCacheForReasonTemplate(reaonRelateKpi);

    }

    /**
     *  SQL模板加载
     */
    private void procKpiSqlTemplate()
    {
        List<KpiSqlTemplate> list=cacheMapper.getKpiSqlTemplateList();

        Map<String, KpiSqlTemplateVO> kpiSqlTemplateMap= Maps.newHashMap();

        KpiSqlTemplateVO vo=null;
        for(KpiSqlTemplate template:list)
        {
            vo=dozerBeanMapper.map(template,KpiSqlTemplateVO.class);

            vo.setDriverTableMapping( Splitter.on(";").trimResults().withKeyValueSeparator(",").split(template.getDriverTables()));
            kpiSqlTemplateMap.put(template.getSqlTemplateCode(),vo);
        }
        KpiCacheManager.getInstance().setCacheForKpiSqlTemplate(kpiSqlTemplateMap);
    }

    /**
     * 所有的DIM信息加载
     */
    private void procDimListLoad()
    {
        List<DimConfigInfo> dimConfigList=cacheMapper.getAllDimConfig();

        KpiCacheManager.getInstance().setCacheForDimConfigList(dimConfigList);
    }

    /**
     * 所有维度的JOIN信息加载到内存
     */
    private void ProcDimJoinInfoLoad()
    {
         //获取所有的JOIN信息列表
        List<DimJoinRelationInfo> joinList=cacheMapper.getDimJoinRelationList();

        List<DimJoinVO> voList= Lists.newArrayList();
        for(DimJoinRelationInfo dimJoinRelationInfo:joinList)
        {
            voList.add(dozerBeanMapper.map(dimJoinRelationInfo,DimJoinVO.class));
        }
        KpiCacheManager.getInstance().setDimJoinList(voList);
    }
}
