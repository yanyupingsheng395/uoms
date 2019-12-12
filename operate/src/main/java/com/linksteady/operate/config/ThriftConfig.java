package com.linksteady.operate.config;

import com.linksteady.operate.thrift.ActivityThriftClient;
import com.linksteady.operate.thrift.InsightThriftClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThriftConfig {

    @Value("${thrift.server.host}")
    private String host;
    @Value("${thrift.server.port}")
    private int port;

    @Bean(initMethod = "init")
    public ActivityThriftClient jazzClient() {
        ActivityThriftClient thriftClient = new ActivityThriftClient();
        thriftClient.setHost(host);
        thriftClient.setPort(port);
        return thriftClient;
    }

    @Bean(initMethod = "init")
    public InsightThriftClient insightThriftClient() {
        InsightThriftClient thriftClient = new InsightThriftClient();
        thriftClient.setHost(host);
        thriftClient.setPort(port);
        return thriftClient;
    }
}
