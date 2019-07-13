package com.linksteady.system.config;

import com.linksteady.system.shiro.ShiroProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "sys")
@Component
public class SystemProperties {

    private ShiroProperties shiro = new ShiroProperties();

    private String timeFormat = "yyyy-MM-dd HH:mm:ss";

    private boolean openAopLog = true;

    private boolean validateThrift = false;

    public ShiroProperties getShiro() {
        return shiro;
    }

    public void setShiro(ShiroProperties shiro) {
        this.shiro = shiro;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public boolean isOpenAopLog() {
        return openAopLog;
    }

    public void setOpenAopLog(boolean openAopLog) {
        this.openAopLog = openAopLog;
    }

    public boolean isValidateThrift() {
        return validateThrift;
    }

    public void setValidateThrift(boolean validateThrift) {
        this.validateThrift = validateThrift;
    }
}
