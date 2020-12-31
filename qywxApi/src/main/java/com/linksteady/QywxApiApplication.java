package com.linksteady;
import com.linksteady.smp.starter.annotation.EnableExceptionNotice;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

@SpringBootApplication
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass=true,exposeProxy=true)
@MapperScan("com.linksteady.**.dao")
@EnableAsync
@EnableExceptionNotice
@Slf4j
public class QywxApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(QywxApiApplication.class, args);
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
