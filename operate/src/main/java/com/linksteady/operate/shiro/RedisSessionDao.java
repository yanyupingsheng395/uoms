package com.linksteady.operate.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author hxcao
 * @date 2020/8/29
 */
public class RedisSessionDao extends EnterpriseCacheSessionDAO {

    private RedisTemplate<Serializable, Session> redisTemplate;

    private int expireIn;

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = super.doCreate(session);
        redisTemplate.opsForValue().set(sessionId, session, expireIn, TimeUnit.SECONDS);


        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = super.doReadSession(sessionId);
        if(session == null) {
            session = redisTemplate.opsForValue().get(sessionId);
        }
        return session;
    }

    @Override
    protected void doUpdate(Session session) {
        redisTemplate.opsForValue().set(session.getId(), session, expireIn, TimeUnit.SECONDS);
    }

    @Override
    protected void doDelete(Session session) {
        redisTemplate.delete(session.getId());
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setExpire(int expireIn) {
        this.expireIn = expireIn;
    }
}
