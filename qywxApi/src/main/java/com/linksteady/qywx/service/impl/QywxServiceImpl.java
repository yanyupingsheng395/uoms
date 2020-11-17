package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.ParamMapper;
import com.linksteady.qywx.domain.QywxParam;
import com.linksteady.qywx.domain.WxError;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.storage.impl.RedisConfigStorageImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;


@Service
@Slf4j
public class QywxServiceImpl implements QywxService {

    @Autowired
    JedisPool jedisPool;

    @Autowired
    ParamMapper paramMapper;

    private RedisConfigStorageImpl redisConfigStorage;

    @PostConstruct
    public void init() throws Exception
    {
        RedisConfigStorageImpl redisConfigStorage=new RedisConfigStorageImpl(jedisPool);
        QywxParam qywxParam=paramMapper.getQywxParam();

        if(null==qywxParam)
        {
            throw new Exception("参数表配置错误");
        }
        if(null!=qywxParam&&!StringUtils.isEmpty(qywxParam.getCorpId()))
        {
            redisConfigStorage.setCorpId(qywxParam.getCorpId());
        }
        if(null!=qywxParam&&!StringUtils.isEmpty(qywxParam.getSecret()))
        {
            redisConfigStorage.setSecret(qywxParam.getSecret());
        }
        this.redisConfigStorage=redisConfigStorage;
    }

    @Override
    public RedisConfigStorageImpl getRedisConfigStorage() {
        return redisConfigStorage;
    }

    @Override
    public boolean checkSignature(String msgSignature, String timestamp, String nonce, String data) {
//        try {
//            log.debug("校验签名是否正确:apptoken:{},生成的签名：{},微信的签名:{}",this.redisConfigStorage.getAppToken(),
//                    SHA1.gen(this.redisConfigStorage.getAppToken(), timestamp, nonce, data),
//                    msgSignature);
//            log.debug("参数为{},{},{},{}",timestamp,nonce,data,msgSignature);
//            return SHA1.gen(this.redisConfigStorage.getAppToken(), timestamp, nonce, data)
//                    .equals(msgSignature);
//        } catch (Exception e) {
//            log.error("Checking signature failed, and the reason is :" + e.getMessage());
//            return false;
//        }
        return true;
    }

    @Override
    public synchronized String getAccessToken() throws WxErrorException {
        String accessToken=this.redisConfigStorage.getAccessToken();

        if(StringUtils.isEmpty(accessToken))
        {
            log.debug("开始获取accessToken");
            if(StringUtils.isEmpty(this.redisConfigStorage.getCorpId())||StringUtils.isEmpty(this.redisConfigStorage.getSecret()))
            {
                throw new WxErrorException(WxError.builder().errorCode(-999).errorMsg("尚未配置企业微信公司ID或应用秘钥").build());
            }
            String requestUrl= String.format(WxPathConsts.GET_TOKEN,this.redisConfigStorage.getCorpId(),this.redisConfigStorage.getSecret());
            String resultContent=OkHttpUtil.getRequest(redisConfigStorage.getApiUrl(requestUrl));

            log.debug("获取accessToken的返回结果为:{}",resultContent);
            JSONObject jsonObject = JSON.parseObject(resultContent);
            WxError error = WxError.fromJsonObject(jsonObject);
            if (error.getErrorCode() != 0) {
                throw new WxErrorException(error);
            }

            accessToken = jsonObject.getString("access_token");
            Integer expiresIn = jsonObject.getInteger("expires_in");
            this.redisConfigStorage.setAccessToken(accessToken, expiresIn);
        }
        return this.redisConfigStorage.getAccessToken();
    }

    @Override
    public synchronized void  updateCorpInfo(String corpId, String secret) {
        //更新到数据库
        paramMapper.updateCorpInfo(corpId,secret);
        //更新到redis
        redisConfigStorage.setCorpId(corpId);
        redisConfigStorage.setSecret(secret);

        //失效accessToken
        redisConfigStorage.expireAccessToken();

    }

    @Override
    public String getEcEventToken() {
        String ecEventToken=this.redisConfigStorage.getEcEventToken();
        if(StringUtils.isEmpty(ecEventToken))
        {
            QywxParam qywxParam=paramMapper.getQywxParam();
            ecEventToken=qywxParam==null?"":qywxParam.getEcEventToken();
            if(!StringUtils.isEmpty(ecEventToken)){
                this.redisConfigStorage.setEcEventToken(ecEventToken);
            }
        }
        return this.redisConfigStorage.getEcEventToken();
    }

    @Override
    public String getEcEventAesKey() {
        String ecEventAesKey=this.redisConfigStorage.getEcEventAesKey();

        if(StringUtils.isEmpty(ecEventAesKey))
        {
            QywxParam qywxParam=paramMapper.getQywxParam();
            ecEventAesKey=qywxParam==null?"":qywxParam.getEcEventAesKey();
            if(!StringUtils.isEmpty(ecEventAesKey)) {
                this.redisConfigStorage.setEcEventAesKey(ecEventAesKey);
            }
        }
        return this.redisConfigStorage.getEcEventAesKey();
    }

    @Override
    public synchronized void updateContact(String eventToken, String eventAesKey) {
        //更新到数据库
        paramMapper.updateContact(eventToken,eventAesKey);
        this.redisConfigStorage.setEcEventToken(eventToken);
        this.redisConfigStorage.setEcEventAesKey(eventAesKey);
    }
}
