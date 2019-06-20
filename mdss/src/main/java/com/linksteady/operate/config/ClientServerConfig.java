package com.linksteady.operate.config;

import com.linksteady.common.service.OpenApiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

/**
 * @author hxcao
 * @date 2019-06-19
 */
@Configuration
public class ClientServerConfig {

    /**
     * 服务端提供的服务URL
     */
    private String serverServiceUrl = "http://localhost:9091/api/serverUrl";

    @Bean
    public HttpInvokerProxyFactoryBean testService() {
        HttpInvokerProxyFactoryBean httpInvokerProxyFactoryBean = new HttpInvokerProxyFactoryBean();
        httpInvokerProxyFactoryBean.setServiceUrl(serverServiceUrl);
        httpInvokerProxyFactoryBean.setServiceInterface(OpenApiService.class);
        return httpInvokerProxyFactoryBean;
    }
}
