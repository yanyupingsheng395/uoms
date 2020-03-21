package com.linksteady.common.config;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 缓存字典内容的缓存管理类
 */
public class DictCacheManager  {

    private static DictCacheManager dictCacheManager;

    /**
     * 第一个参数为缓存的KEY  对应dict中typeCode
     * 第二个MAP为具体的key-value值，对应dict中的code value
     */
    private static Map<String, Map<String,String>> dictMap = Maps.newHashMap();

    public static DictCacheManager getInstance() {
        if (null == dictCacheManager) {
            synchronized (DictCacheManager.class) {
                if (null == dictCacheManager) {
                    dictCacheManager = new DictCacheManager();
                }
            }
        }
        return dictCacheManager;
    }

    public Map<String,String> getdictMap(String typeCode) {
        return dictMap.get(typeCode);
    }

    public synchronized void setDictMap(String typeCode,Map<String,String> content) {
        dictMap.remove(typeCode);
        dictMap.put(typeCode,content);
    }

}
