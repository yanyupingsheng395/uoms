package com.linksteady.operate.config;

import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DozerConfig {

    @Bean(name = "org.dozer.Mapper")
    public DozerBeanMapper dozer() {
        //这里是配置文件的路径
        List<String> mappingFiles = Arrays.asList("dozer/dozer-mapping.xml");
        DozerBeanMapper dozerBean = new DozerBeanMapper();
        dozerBean.setMappingFiles(mappingFiles);

        System.out.println("初始化dozer");
        return dozerBean;
    }
}
