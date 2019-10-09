package com.linksteady;

import com.linksteady.lognotice.annotation.EnableExceptionNotice;
import com.linksteady.mdss.config.SystemProperties;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.linksteady.*.dao")
@EnableConfigurationProperties({SystemProperties.class})
@EnableCaching
@EnableAsync
@EnableExceptionNotice
@Slf4j
public class MdssApplication {

    public static void main(String[] args) {
        SpringApplication.run(MdssApplication.class, args);
        log.info("mdss started up successfully at {} {}", LocalDate.now(), LocalTime.now());
    }
}