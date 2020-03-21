package com.linksteady.operate.config;

import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.domain.PushProperties;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.PushPropertiesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 判断t_config配置数据是否能从redis中加载到，如果加载不到，则报错
 */
@Component
@Slf4j
public class LoadConfig implements CommandLineRunner {

    @Autowired
    ConfigService configService;

    @Autowired
    PushPropertiesService pushPropertiesService;

    @Override
    public void run(String... args) throws Exception {
        //判断redis中TCONFIG这个hashkey是否存在
        if(!configService.configExists())
        {
            throw new LinkSteadyException("无法正确加载到配置，请检查");
        }
    }

    /**
     * 读取推送的配置信息
     *
     */
    @Bean(name="pushProperties")
    public PushProperties pushProperties() throws Exception
    {
        //初始化配置对象
        PushProperties pushProperties=new PushProperties();
        pushPropertiesService.initProperties(pushProperties,"init");

        return pushProperties;
    }


}
