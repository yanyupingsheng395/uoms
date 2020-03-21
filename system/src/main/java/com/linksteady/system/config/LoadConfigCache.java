package com.linksteady.system.config;


import com.linksteady.common.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LoadConfigCache implements CommandLineRunner {

    @Autowired
    ConfigService configService;

    @Override
    public void run(String... args) throws Exception {
        //加载配置 从数据库加载配置到redis中
        configService.loadConfigToRedis();
    }
}
