package com.linksteady.mdss.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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