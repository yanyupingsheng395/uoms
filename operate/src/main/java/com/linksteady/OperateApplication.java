package com.linksteady;

import com.linksteady.common.config.SystemProperties;
import com.linksteady.common.util.SpringContextUtils;
import com.linksteady.smp.starter.annotation.EnableExceptionNotice;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

@SpringBootApplication
@EnableTransactionManagement
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.linksteady.**.dao")
@EnableCaching
@EnableAsync
@EnableExceptionNotice
@Slf4j
public class OperateApplication {

	public static void main(String[] args) {
		SpringApplication.run(OperateApplication.class, args);
		SystemProperties systemProperties= (SystemProperties) SpringContextUtils.getBean(SystemProperties.class);
		log.info("用户成长模块.[{}]",systemProperties.isDemoEnvironment()==true?"演示环境":"非演示环境");
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
