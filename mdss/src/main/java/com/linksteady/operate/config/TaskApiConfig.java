package com.linksteady.operate.config;

import com.linksteady.common.service.OpenApiService;
import com.linksteady.common.service.TaskApiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

/**
 * @author hxcao
 * @date 2019-07-08
 *
 * 计算GMV目标任务的api，对接调度系统
 */
@Configuration
public class TaskApiConfig {

    @Bean("/api/taskApi")
    public HttpInvokerServiceExporter testService(TaskApiService service) {
        HttpInvokerServiceExporter httpInvokerServiceExporter = new HttpInvokerServiceExporter();
        httpInvokerServiceExporter.setService(service);
        httpInvokerServiceExporter.setServiceInterface(TaskApiService.class);
        return httpInvokerServiceExporter;
    }
}
