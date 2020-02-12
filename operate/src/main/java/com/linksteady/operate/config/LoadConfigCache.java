package com.linksteady.operate.config;

import com.linksteady.operate.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LoadConfigCache implements CommandLineRunner {

    @Autowired
    ConfigService configService;

    @Override
    public void run(String... args) throws Exception {
         //加载活跃度
        ConfigCacheManager.getInstance().setPathActiveMap(configService.selectPathActive());

        //加载用户价值
        ConfigCacheManager.getInstance().setUserValueMap(configService.selectUserValue());
        //加载生命周期
        ConfigCacheManager.getInstance().setLifeCycleMap(configService.selectLifeCycle());
        //加载其余配置
        ConfigCacheManager.getInstance().setConfigMap(configService.selectCommonConfig());
    }
}
