package com.linksteady.jobmanager.config;

import com.linksteady.common.constant.CommonConstant;
import com.linksteady.common.service.OpenApiService;
import com.linksteady.common.service.TaskApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-09
 */
@Configuration
@AutoConfigureAfter(RedisConfig.class)
public class TaskApiClientConfig {

    /**
     * 服务端提供的服务URL
     */
    private String domain = CommonConstant.DIAG_CODE;

    private String url = "/api/taskApi";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 这里需要注意system 和 mdss 的redis BusinessDataBase配置信息一致，否则无法获取codeUrlMap
     * @return
     * @throws Exception
     */
    @Bean
    public HttpInvokerProxyFactoryBean taskApiService() throws Exception{
        HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();
        Map<String, String> applicationInfoMap = (Map<String, String>)redisTemplate.opsForValue().get("applicationInfoMap");
        String serviceUrl = applicationInfoMap.get(domain) + "/" + url;
        httpInvokerProxyFactoryBean.setServiceUrl(serviceUrl);
        httpInvokerProxyFactoryBean.setServiceInterface(TaskApiService.class);
        return httpInvokerProxyFactoryBean;
    }
}
