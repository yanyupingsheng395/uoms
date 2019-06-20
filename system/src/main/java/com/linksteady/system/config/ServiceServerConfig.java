package com.linksteady.system.config;

import com.linksteady.common.service.OpenApiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

/**
 * @author hxcao
 * @date 2019-06-19
 */

@Configuration
public class ServiceServerConfig {

    @Bean("/api/serverUrl")
    public HttpInvokerServiceExporter testService(OpenApiService service) {
        HttpInvokerServiceExporter httpInvokerServiceExporter = new HttpInvokerServiceExporter();
        httpInvokerServiceExporter.setService(service);
        httpInvokerServiceExporter.setServiceInterface(OpenApiService.class);
        return httpInvokerServiceExporter;
    }
}
