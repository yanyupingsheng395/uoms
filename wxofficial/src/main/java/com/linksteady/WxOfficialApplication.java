package com.linksteady;
import com.linksteady.smp.starter.annotation.EnableExceptionNotice;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author hxcao
 * @date 2020/4/15
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.linksteady.**.dao")
@EnableCaching
@EnableAsync
@EnableExceptionNotice
@Slf4j
public class WxOfficialApplication {

    public static void main(String[] args) {
        SpringApplication.run(WxOfficialApplication.class, args);
        log.info("微信端系统启动成功.");
    }
}
