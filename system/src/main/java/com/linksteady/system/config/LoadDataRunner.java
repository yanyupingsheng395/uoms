package com.linksteady.system.config;

import com.linksteady.common.domain.SysInfo;
import com.linksteady.common.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-06-21
 */
@Component
public class LoadDataRunner implements CommandLineRunner {

    @Autowired
    private SystemService systemService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        ValueOperations op=redisTemplate.opsForValue();
        /**
         * 缓存所有的业务系统信息 code:sysInfo 缓存到redis
         */
        Map<String, SysInfo> sysInfoMap=systemService.findAllSystem().stream().collect(Collectors.toMap(SysInfo::getCode, sysInfo -> sysInfo));
        op.set("sysInfoMap",sysInfoMap);
    }
}
