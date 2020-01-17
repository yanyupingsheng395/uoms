package com.linksteady.operate.config;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class ConfigCacheManager  {

    private static ConfigCacheManager configCacheManager;

    /**
     * 活跃度
     */
    private static Map<String, String> pathActiveMap = Maps.newLinkedHashMap();

    /**
     * 价值
     * @return
     */
    private static Map<String, String> userValueMap = Maps.newLinkedHashMap();


    /**
     * 生命周期
     * @return
     */
    private static Map<String, String> lifeCycleMap = Maps.newLinkedHashMap();

    /**
     * 加载所有的配置
     * @return
     */
    private static Map<String,String> configMap=Maps.newHashMap();


    public static ConfigCacheManager getInstance() {
        if (null == configCacheManager) {
            synchronized (ConfigCacheManager.class) {
                if (null == configCacheManager) {
                    configCacheManager = new ConfigCacheManager();
                }
            }
        }
        return configCacheManager;
    }


    public Map<String, String> getPathActiveMap() {
        return pathActiveMap;
    }

    public void setPathActiveMap(List<Map<String, String>> param) {
        pathActiveMap.clear();
        for(Map<String,String> temp:param)
        {
            pathActiveMap.put(temp.get("CODE"),temp.get("VALUE"));
        }
    }

    public Map<String, String> getUserValueMap() {
        return userValueMap;
    }

    public void setUserValueMap(List<Map<String, String>> param) {
        userValueMap.clear();
        for(Map<String,String> temp:param)
        {
            userValueMap.put(temp.get("CODE"),temp.get("VALUE"));
        }
    }

    public Map<String, String> getLifeCycleMap() {
        return lifeCycleMap;
    }

    public void setLifeCycleMap(List<Map<String, String>> param) {
        lifeCycleMap.clear();
        for(Map<String,String> temp:param)
        {
            lifeCycleMap.put(temp.get("CODE"),temp.get("VALUE"));
        }
    }

    public Map<String, String> getConfigMap() {
        return configMap;
    }

    public void setConfigMap(List<Map<String, String>> param) {
        configMap.clear();
        for(Map<String,String> temp:param)
        {
            configMap.put(temp.get("NAME"),temp.get("VALUE"));
        }
    }
}
