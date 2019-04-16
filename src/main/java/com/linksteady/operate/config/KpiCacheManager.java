package com.linksteady.operate.config;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 对一些频繁使用的值进行缓存,在系统启动的时候在LoadConfigRunner中进行初始化，目前未实现更新机制，后续考虑增加定时类更新
 * codeNamePair  诊断功能的 指标列表；
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class KpiCacheManager {

    private static KpiCacheManager kpiCacheManager;

    private static Map<String, String> codeNamePair = Maps.newLinkedHashMap();  //kpi code-name对

    private static Map<String, String> codeFomularPair =Maps.newLinkedHashMap();  //kpi code-fomular对

    private static Map<String, Object> kpidismant = Maps.newLinkedHashMap();  //每个指标 及其对应的乘法公式列表

    private static Map<String, String> diagDimList = Maps.newLinkedHashMap();  //诊断 维度列表

    private static Map<String, Object> diagDimValueList = Maps.newLinkedHashMap();  //诊断 维度及其值列表

    private static Map<String, String> reaonDimList = Maps.newLinkedHashMap();  //诊断 维度列表

    private static Map<String, Object> reasonDimValueList = Maps.newLinkedHashMap();  //诊断 维度及其值列表

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
    public Map<String,String> getCodeNamePair()
    {
        return codeNamePair;
    }

    // kpicode kpiname的对
    public void setCacheMap(Map<String,String> map,String type)
    {
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();

        if("codeNamePair".equals(type))  //诊断kpi的键值对
        {
            //首先清空
            codeNamePair.clear();
            while (it.hasNext())
            {
                String key=it.next();
                codeNamePair.put(key,map.get(key));
            }
        }else if("codeFomularPair".equals(type))
        {
              codeFomularPair.clear();
            while (it.hasNext())
            {
                String key=it.next();
                codeFomularPair.put(key,map.get(key));
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
        }
    }

    // kpicode kpiname的对
    public void setCacheMapForObject(Map<String,Object> map,String type) {
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();

        if ("kpidismant".equals(type)) {
            kpidismant.clear();
            while (it.hasNext()) {
                String key = it.next();
                kpidismant.put(key, map.get(key));
            }
        }else if("diagDimValueList".equals(type)) {
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



    public Map<String,String> getCodeFomularPair()
    {
        return codeFomularPair;
    }

    public Map<String,String> getDiagDimList()
    {
        return diagDimList;
    }

    public Map<String,Object> getKpiDismant()
    {
        return kpidismant;
    }

    public Map<String,Object> getDiagDimValueList()
    {
        return diagDimValueList;
    }

    public Map<String,String> getReasonDimList()
    {
        return reaonDimList;
    }

    public Map<String,Object> getReasonDimValueList()
    {
        return reasonDimValueList;
    }



}
