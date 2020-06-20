package com.linksteady.operate.config;

import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.ShortUrlMapper;
import com.linksteady.operate.domain.ShortUrlInfo;
import com.linksteady.operate.exception.LinkSteadyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 判断t_config配置数据是否能从redis中加载到，如果加载不到，则报错
 */
@Component
@Slf4j
public class LoadConfig implements CommandLineRunner {

    @Autowired
    ConfigService configService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ShortUrlMapper shortUrlMapper;

    private String redisDataKey = "SHORT_URL_KEY";

    @Override
    public void run(String... args) throws Exception {
        //判断redis中TCONFIG这个hashkey是否存在
        if(!configService.configExists())
        {
//            throw new LinkSteadyException("无法正确加载到配置，请检查");
        }
        //将短链加载到redis中去
        setShortUrlToRedis();
    }

    /**
     * 重新同步短链数据到redis
     */
    private void setShortUrlToRedis() {
        if(redisTemplate.hasKey(redisDataKey)) {
           redisTemplate.delete(redisDataKey);
        }
        log.info("开始将短链的数据同步到redis");
        HashOperations hashOperations = redisTemplate.opsForHash();
        List<ShortUrlInfo> shortUrlInfos = shortUrlMapper.getDataList();

        if(null==shortUrlInfos||shortUrlInfos.size()==0)
        {
            redisTemplate.expire(redisDataKey,1L, TimeUnit.DAYS);
        }else
        {
            shortUrlInfos.stream().forEach(x -> {
                hashOperations.putIfAbsent(redisDataKey, x.getLongUrl(), x.getShortUrl());
            });
            //已最早失效的那个url的失效时间作为整个redisDataKey的失效时间
            ShortUrlInfo shortUrlInfo = shortUrlInfos.stream().min(Comparator.comparing(ShortUrlInfo::getValidateDate)).get();
            redisTemplate.expireAt(redisDataKey, shortUrlInfo.getValidateDate());
        }

        log.info("结束同步短链数据到redis");
    }

}
