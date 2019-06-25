package com.linksteady.operate.config;

import com.linksteady.operate.config.SystemProperties;
import com.linksteady.operate.thrift.ThriftClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 验证thrift 接口是否正常
 * @author
 */
@Component
@Order(1)
@Slf4j
public class PingThriftRunner implements CommandLineRunner {

    @Autowired
    ThriftClient thriftClient;

    @Autowired
    private SystemProperties systemProperties;

    @Override
    public void run(String... args) throws Exception {
        if(systemProperties.isValidateThrift())
        {
            try {
                thriftClient.open();
                thriftClient.getThriftService().ping();
                thriftClient.close();
            } catch (TException e) {
                log.error("thrift服务无法正常连接，请检查!",e);
                throw new Exception("thrift服务无法连接，系统停止运行！");
            }
        }

    }
}