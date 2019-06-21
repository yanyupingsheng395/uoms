package com.linksteady.system.config;

import com.linksteady.system.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-06-21
 */
@Component
public class LoadDataRunner implements CommandLineRunner {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        Map<String, String> codeUrlMap = applicationService.getCodeAndUrl();
        redisTemplate.opsForValue().set("codeUrlMap:20190621", codeUrlMap);
    }
}
