package com.linksteady;

import com.linksteady.lognotice.annotation.EnableExceptionNotice;
import com.linksteady.operate.config.SystemProperties;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.linksteady.*.dao")
@EnableConfigurationProperties({SystemProperties.class})
@EnableCaching
@EnableAsync
@EnableExceptionNotice
@Slf4j
public class OperateApplication {

	public static void main(String[] args) {
		SpringApplication.run(OperateApplication.class, args);
		log.info("用户成长系统启动成功.");
	}
}
