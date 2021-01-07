package com.linksteady.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sys")
public class SystemProperties {

    private String timeFormat = "yyyy-MM-dd HH:mm:ss";

    private boolean openAopLog = true;

    private boolean validateThrift = false;

    private boolean demoEnvironment = false;

    private String thriftServerHost ="127.0.0.1";

    private int thriftServerPort = 7778;
}
