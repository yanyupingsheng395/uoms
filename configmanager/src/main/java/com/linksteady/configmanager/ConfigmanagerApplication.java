package com.linksteady.configmanager;

import com.linksteady.lognotice.annotation.EnableExceptionNotice;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.linksteady.*.dao")
@EnableCaching
@EnableAsync
@EnableExceptionNotice
@Slf4j
public class ConfigmanagerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ConfigmanagerApplication.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.run(args);
        log.info("配置管理模块 started up successfully at {} {}", LocalDate.now(), LocalTime.now());
    }
}




