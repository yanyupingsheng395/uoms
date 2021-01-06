package com.linksteady.system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linksteady.common.config.SystemProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
@SuppressWarnings("unchecked")
public class WebConfig {

    @Bean
    public ObjectMapper getObjectMapper(SystemProperties systemProperties) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(systemProperties.getTimeFormat()));
        return mapper;
    }

}