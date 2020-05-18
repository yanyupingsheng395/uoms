package com.linksteady.wxofficial.config;

import com.linksteady.common.service.ConfigService;
import com.linksteady.wxofficial.common.exception.LinkSteadyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author hxcao
 * @date 2020/4/17
 */
@Component
@Slf4j
public class LoadConfig implements CommandLineRunner {

    @Autowired
    private ConfigService configService;
    @Autowired
    private WxProperties wxProperties;

    @Override
    public void run(String... args) throws Exception {
        configService.loadConfigToRedis();
        if(!configService.configExists())
        {
            throw new LinkSteadyException("无法正确加载到配置，请检查");
        }
        checkAppId();
    }

    private void checkAppId(){
        String appId = configService.getValueByName("op.wx.appid");
        if(StringUtils.isNotEmpty(appId)) {
            wxProperties.setAppId(appId);
        }else {
            throw new RuntimeException("检测到T_CONFIG中未配置appId");
        }
    }
}
