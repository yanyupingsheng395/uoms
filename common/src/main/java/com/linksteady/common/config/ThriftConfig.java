package com.linksteady.common.config;

import com.linksteady.common.thrift.ThriftClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThriftConfig {

    @Autowired
    SystemProperties systemProperties;

    @Bean(initMethod = "init")
    public ThriftClient jazzClient() {
        ThriftClient thriftClient = new ThriftClient();
        thriftClient.setHost(systemProperties.getThriftServerHost());
        thriftClient.setPort(systemProperties.getThriftServerPort());
        return thriftClient;
    }

}
