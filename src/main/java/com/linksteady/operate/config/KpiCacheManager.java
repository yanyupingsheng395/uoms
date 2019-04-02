package com.linksteady.operate.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class KpiCacheManager {

    private static KpiCacheManager kpiCacheManager;

    private static Map<String, String> codeNamePair = new HashMap<String, String>();  //kpi code-name对

    private static Map<String, String> codeFomularPair = new HashMap<String, String>();  //kpi code-fomular对

    private static Map<String, Object> kpidismant = new HashMap<String, Object>();  //每个指标 及其对应的乘法公式列表

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
    public void setCodeNamePair(Map<String,String> map)
    {
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();
        while (it.hasNext())
        {
            String key=it.next();
            codeNamePair.put(key,map.get(key));
        }
    }

    //获取拆解
    public Map<String,String> getCodeFomularPair()
    {
        return codeFomularPair;
    }

    //放入 kpicode 公式 的对
    public void setCodeFomularPair(Map<String,String> cfp)
    {
        Set<String> set2 = cfp.keySet();
        Iterator<String> it2 = set2.iterator();
        while (it2.hasNext())
        {
            String key=it2.next();
            codeFomularPair.put(key,cfp.get(key));
        }
    }


    //获取拆解
    public Map<String,Object> getKpiDismant()
    {
        return kpidismant;
    }

    //放入kpi拆解信息
    public void setKpiDismant(Map<String,Object> kst)
    {
        Set<String> set3 = kst.keySet();
        Iterator<String> it3 = set3.iterator();
        while (it3.hasNext())
        {
            String key=it3.next();
            kpidismant.put(key,kst.get(key));
        }
    }


}
