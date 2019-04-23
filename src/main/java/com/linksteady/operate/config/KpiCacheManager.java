package com.linksteady.operate.config;

import com.google.common.collect.Maps;
import com.linksteady.operate.domain.KpiDismantInfo;
import com.linksteady.operate.domain.ReasonTemplateInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 对一些频繁使用的值进行缓存,在系统启动的时候在LoadConfigRunner中进行初始化，目前未实现更新机制，后续考虑增加定时类更新
 * @author huang
 */
public class KpiCacheManager {

    private static KpiCacheManager kpiCacheManager;

    /**
     * 所有诊断用到的指标CODE，名称的键-值对。 kpi code-name对
     */
    private static Map<String, String> diagKpiList = Maps.newLinkedHashMap();

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
     *诊断用到的维度值列表 key为维度编码 value为一个map 此map中key为维度值编码，value为维度值名称
     */
    private static Map<String, Map<String,String>> diagDimValueList = Maps.newLinkedHashMap();

    /**
     *原因探究用到的 指标CODE,名称的键值对
     */
    private static Map<String, String> reasonKpiList = Maps.newLinkedHashMap();

    /**
     *原因探究用到维度列表 key为维度编码 value为维度名称
     */
    private static Map<String, String> reaonDimList = Maps.newLinkedHashMap();

    /**
     *原因探究用到的维度值列表 key为维度编码 value为一个map 此map中key为维度值编码，value为维度值名称
     */
    private static Map<String, Map<String,String>> reasonDimValueList = Maps.newLinkedHashMap();

    /**
     *探究的REASON_KPI_CODE -  ReasonTemplateInfo(名称,排序号)
     */
    private static Map<String, ReasonTemplateInfo> reasonRelateKpiList = Maps.newHashMap();

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
    public Map<String,String> getDiagKpiList()
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

        //诊断kpi的键值对
        if("diagKpiList".equals(type))
        {
            //首先清空
            diagKpiList.clear();
            while (it.hasNext())
            {
                String key=it.next();
                diagKpiList.put(key,map.get(key));
            }
        }else if("DiagcodeFomularList".equals(type))
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
        }else if("reasonKpiList".equals(type))
        {
            reasonKpiList.clear();
            while (it.hasNext())
            {
                String key=it.next();
                reasonKpiList.put(key,map.get(key));
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
                String key = it.next();
                diagDimValueList.put(key, map.get(key));
            }
        }else if("reasonDimValueList".equals(type)) {
            reasonDimValueList.clear();
            while (it.hasNext()) {
                String key = it.next();
                reasonDimValueList.put(key, map.get(key));
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

    public Map<String,Map<String,String>> getDiagDimValueList()
    {
        return diagDimValueList;
    }

    public Map<String, String> getReasonKpiList() {
        return reasonKpiList;
    }

    public Map<String,String> getReasonDimList()
    {
        return reaonDimList;
    }

    public Map<String,Map<String,String>> getReasonDimValueList()
    {
        return reasonDimValueList;
    }

    public Map<String, ReasonTemplateInfo> getReasonRelateKpiList() {
        return reasonRelateKpiList;
    }
}
