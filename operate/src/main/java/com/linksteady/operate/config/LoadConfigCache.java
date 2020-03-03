package com.linksteady.operate.config;

import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 判断t_config配置数据是否能从redis中加载到，如果加载不到，则报错
 */
@Component
public class LoadConfigCache implements CommandLineRunner {

    @Autowired
    ConfigService configService;

    @Override
    public void run(String... args) throws Exception {
        //判断redis中TCONFIG这个hashkey是否存在
        if(!configService.configExists())
        {
            throw new LinkSteadyException("无法正确加载到配置，请检查");
        }
    }
}
