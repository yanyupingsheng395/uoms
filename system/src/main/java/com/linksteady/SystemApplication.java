package com.linksteady;

import com.linksteady.lognotice.annotation.EnableExceptionNotice;
import com.linksteady.system.config.SystemProperties;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.linksteady.**.dao")
@EnableConfigurationProperties({SystemProperties.class})
@EnableCaching
@EnableAsync
@EnableExceptionNotice
@Slf4j
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SystemApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run();
        log.info("系统管理模块 started up successfully at {} {}", LocalDate.now(), LocalTime.now());
    }

    /**
     * 添加静态资源md5版本控制
     * @return
     */
    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }
}