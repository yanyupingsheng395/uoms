package com.linksteady.operate.config;

import com.linksteady.operate.thrift.ThriftClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class ThriftConfig {

    @Value("${thrift.server.host}")
    private String host;
    @Value("${thrift.server.port}")
    private int port;

    @Bean(initMethod = "init")
    public ThriftClient jazzClient() {
        ThriftClient thriftClient = new ThriftClient();
        thriftClient.setHost(host);
        thriftClient.setPort(port);
        return thriftClient;
    }
}
