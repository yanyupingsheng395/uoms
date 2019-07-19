package com.linksteady.mdss.config;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.linksteady.mdss.domain.DimConfigInfo;
import com.linksteady.mdss.domain.KpiConfigInfo;
import com.linksteady.mdss.domain.KpiDismantInfo;
import com.linksteady.mdss.domain.ReasonTemplateInfo;
import com.linksteady.mdss.vo.DimJoinVO;
import com.linksteady.mdss.vo.KpiSqlTemplateVO;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 对一些频繁使用的值进行缓存,在系统启动的时候在LoadConfigRunner中进行初始化，目前未实现更新机制，后续考虑增加定时类更新
 * @author huang
 */
public class KpiCacheManager {

    private static KpiCacheManager kpiCacheManager;


    /**
     * 所有指标CODE，NAME值对
     */
    private static Map<String, String> kpiCodeNamePair = Maps.newLinkedHashMap();

    /**
     * 所有诊断用到的指标CODE，指标对象的键-值对。 kpi code-KpiConfigInfo对
     */
    private static Map<String, KpiConfigInfo> diagKpiList = Maps.newLinkedHashMap();

    /**
     * /所有可被乘法拆解的指标CODE - 拆解公式 的键值对。key为指标编码，value为拆解公式。kpi code-fomular对
     */
    private static Map<String, String> DiagcodeFomularList =Maps.newLinkedHashMap();

    /**
     *每个指标及其对应的乘法公式。 key为指标编码，KpiDismantInfo为其对应的拆解公式。每个指标 及其对应的乘法公式列表
     */
    private static Map<String, KpiDismantInfo> kpidismant = Maps.newLinkedHashMap();

    /**
     *诊断用到维度列表 key为维度编码 value为维度名称
     */
    private static Map<String, String> diagDimList = Maps.newLinkedHashMap();

    /**
     * 第一个参数为维度编码(row) 第二个参数为维度值编码(column) 第三个参数为维度值显示名称
     */
    private static Table<String,String,String> diagDimValueList = HashBasedTable.create();

    /**
     *原因探究用到的 指标CODE,指标对象的键值对
     */
    private static Map<String, KpiConfigInfo> reasonKpiList = Maps.newLinkedHashMap();

    /**
     *原因探究用到维度列表 key为维度编码 value为维度名称
     */
    private static Map<String, String> reaonDimList = Maps.newLinkedHashMap();

    /**
     *原因探究用到的维度值列表 key为维度编码 value为一个map 此map中key为维度值编码，value为维度值名称
     * 第一个参数为维度编码(row)  第二个参数为维度编码值 第三个参数为维度值名称
      */
    private static Table<String,String,String> reasonDimValueList = HashBasedTable.create();

    /**
     *探究的REASON_KPI_CODE -  ReasonTemplateInfo(名称,排序号)
     */
    private static Map<String, ReasonTemplateInfo> reasonRelateKpiList = Maps.newHashMap();

    /**
     * SQL模板信息加载
     */
    private static Map<String, KpiSqlTemplateVO> kpiSqlTemplateList = Maps.newHashMap();

    /**
     * 所有的维度配置信息
     */
    private static  List<DimConfigInfo>  dimConfigList= Lists.newArrayList();

    /**
     * 所有表之间的映射信息  第一个参数为驱动表名(row)  第二个参数为维度编码(column) 第三个参数为JOIN的关系VO
     */
    Table<String,String, DimJoinVO> dimJoinList= HashBasedTable.create();



    public static KpiCacheManager getInstance() {
        if (null == kpiCacheManager) {
            synchronized (KpiCacheManager.class) {
                if (null == kpiCacheManager) {
                    kpiCacheManager = new KpiCacheManager();
                }
            }
        }
        return kpiCacheManager;
    }

    //获取缓存
    public Map<String,KpiConfigInfo> getDiagKpiList()
    {
        return diagKpiList;
    }

    /**
     *  kpicode kpiname的对 (适用于value为String的情况)
     * @param map
     * @param type
     */
    public void setCacheMap(Map<String,String> map,String type)
    {
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();

        if("DiagcodeFomularList".equals(type))
        {
            DiagcodeFomularList.clear();
            while (it.hasNext())
            {
                String key=it.next();
                DiagcodeFomularList.put(key,map.get(key));
            }
        }else if("diagDimList".equals(type))
        {
            diagDimList.clear();
            while (it.hasNext())
            {
                String key=it.next();
                diagDimList.put(key,map.get(key));
            }
        }else if("reaonDimList".equals(type))
        {
            reaonDimList.clear();
            while (it.hasNext())
            {
                String key=it.next();
                reaonDimList.put(key,map.get(key));
            }
        }else if("kpiCodeNamePair".equals(type))
        {
            kpiCodeNamePair.clear();
            while (it.hasNext())
            {
                String key=it.next();
                kpiCodeNamePair.put(key,map.get(key));
            }
        }
    }

    /**
     * kpicode kpiname的对(适用于value类型为通用map)
     * @param map
     * @param type
     */
    public void setCacheMapForCommonMmap(Map<String,Map<String,String>> map,String type) {
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();

       if("diagDimValueList".equals(type)) {
            diagDimValueList.clear();
            while (it.hasNext()) {
                String dimCode = it.next();
                Map<String,String> dimValueMap=map.get(dimCode);
                Iterator<String> dimValueIt=dimValueMap.keySet().iterator();
                while(dimValueIt.hasNext())
                {
                    String valueCode=dimValueIt.next();
                    diagDimValueList.put(dimCode,valueCode,dimValueMap.get(valueCode));
                }
            }
        }else if("reasonDimValueList".equals(type)) {
            reasonDimValueList.clear();
            while (it.hasNext()) {
                String dimCode = it.next();
                Map<String,String> dimValueMap=map.get(dimCode);
                Iterator<String> dimValueIt=dimValueMap.keySet().iterator();
                while(dimValueIt.hasNext())
                {
                    String valueCode=dimValueIt.next();
                    reasonDimValueList.put(dimCode,valueCode,dimValueMap.get(valueCode));
                }
            }
        }

    }

    public void setCacheForKpiDismant(Map<String,KpiDismantInfo> map) {
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();
        kpidismant.clear();
        while (it.hasNext()) {
            String key = it.next();
            kpidismant.put(key, map.get(key));
        }
    }

    public void setCacheForReasonTemplate(Map<String,ReasonTemplateInfo> map) {
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();
        reasonRelateKpiList.clear();
        while (it.hasNext()) {
            String key = it.next();
            reasonRelateKpiList.put(key, map.get(key));
        }
    }

    public void setCacheForKpiSqlTemplate(Map<String,KpiSqlTemplateVO> map) {
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();
        kpiSqlTemplateList.clear();
        while (it.hasNext()) {
            String key = it.next();
            kpiSqlTemplateList.put(key, map.get(key));
        }
    }

    public void setCacheForDimConfigList(List<DimConfigInfo> list) {
        dimConfigList.clear();
        Iterator<DimConfigInfo> it = list.iterator();
        while (it.hasNext()) {
            dimConfigList.add(it.next());
        }
    }

    public void setCacheForDiagKpi(Map<String,KpiConfigInfo> map) {
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();

        //首先清空
        diagKpiList.clear();
        while (it.hasNext())
        {
            String key=it.next();
            diagKpiList.put(key,map.get(key));
        }
    }

    public void setCacheForReasonKpi(Map<String,KpiConfigInfo> map) {
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();

        reasonKpiList.clear();
        while (it.hasNext())
        {
            String key=it.next();
            reasonKpiList.put(key,map.get(key));
        }
    }



    public void setDimJoinList(List<DimJoinVO> list) {
        dimJoinList.clear();
        for(DimJoinVO vo:list)
        {
            dimJoinList.put(vo.getDirverTableName(),vo.getDimCode(),vo);
        }
    }


    public Map<String,String> getKpiCodeNamePair()
    {
        return kpiCodeNamePair;
    }

    public Map<String, String> getDiagcodeFomularList() {
        return DiagcodeFomularList;
    }

    public Map<String,String> getDiagDimList()
    {
        return diagDimList;
    }

    public Map<String,KpiDismantInfo> getKpiDismant()
    {
        return kpidismant;
    }

    public Table<String,String,String> getDiagDimValueList()
    {
        return diagDimValueList;
    }

    public Map<String, KpiConfigInfo> getReasonKpiList() {
        return reasonKpiList;
    }

    public Map<String,String> getReasonDimList()
    {
        return reaonDimList;
    }

    public Table<String,String,String> getReasonDimValueList()
    {
        return reasonDimValueList;
    }

    public Map<String, ReasonTemplateInfo> getReasonRelateKpiList() {
        return reasonRelateKpiList;
    }

    public Map<String, KpiSqlTemplateVO> getKpiSqlTemplateList() {
        return kpiSqlTemplateList;
    }

    public List<DimConfigInfo> getDimConfigList() {
        return dimConfigList;
    }

    public Table<String, String, DimJoinVO> getDimJoinList() {
        return dimJoinList;
    }
}
