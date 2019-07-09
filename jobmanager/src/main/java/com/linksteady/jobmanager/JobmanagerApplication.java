package com.linksteady.jobmanager;

import com.linksteady.jobmanager.config.SystemProperties;
import com.linksteady.lognotice.annotation.EnableExceptionNotice;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootApplication(scanBasePackages = {"com.linksteady.**"})
@EnableTransactionManagement
@MapperScan("com.linksteady.*.dao")
@EnableConfigurationProperties({SystemProperties.class})
@EnableCaching
@EnableAsync
@EnableExceptionNotice
@Slf4j
public class JobmanagerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JobmanagerApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
        log.info("系统调度模块 started up successfully at {} {}", LocalDate.now(), LocalTime.now());
    }
}




