package com.linksteady.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

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