package com.linksteady.operate.config;

import com.linksteady.operate.service.RedisMessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * redis 发布-订阅
 */
@Component
public class RedisListenerConfig {

    /**
     * 接收推送心跳信息的通道名称
     */
    private static final String PUSH_HEART_BEAT_CHANNEL="pushHeartbeat";

    @Bean
    RedisMessageListenerContainer container(JedisConnectionFactory jedisConnectionFactory,
                                            MessageListenerAdapter listenerAdapter){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(PUSH_HEART_BEAT_CHANNEL));
        return container;
    }

    /**
     * 消息监听器适配器，绑定消息处理器，利用反射技术调用消息处理器的业务方法
     * @param redisMessageService
     * @return
     */
    @Bean
    MessageListenerAdapter listenerAdapter(RedisMessageService redisMessageService){
        return new MessageListenerAdapter(redisMessageService, "receivePushHeartBeat");
    }

}
