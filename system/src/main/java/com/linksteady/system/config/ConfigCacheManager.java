package com.linksteady.system.config;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class ConfigCacheManager  {

    private static ConfigCacheManager configCacheManager;

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
