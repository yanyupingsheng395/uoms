package com.linksteady.qywx.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置全局缓存参数，3600秒过期，最大个数1000
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager caffeineCacheManager=new CaffeineCacheManager();
        Caffeine caffeine= Caffeine.newBuilder().initialCapacity(30)
                .maximumSize(1000)
                .expireAfterWrite(1, TimeUnit.DAYS);
        caffeineCacheManager.setCaffeine(caffeine);
        caffeineCacheManager.setAllowNullValues(true);
        caffeineCacheManager.setCacheNames(getNames());
        return caffeineCacheManager;
    }

    List<String> getNames()
    {
        return new ArrayList(){{
            this.add("qywx");
        }};
    }
}
