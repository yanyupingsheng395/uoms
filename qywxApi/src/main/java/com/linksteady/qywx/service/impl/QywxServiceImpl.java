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

        //将数据库中的代码同步到redis中
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

        if(null!=qywxParam&&!StringUtils.isEmpty(qywxParam.getEcEventToken()))
        {
            redisConfigStorage.setEcEventToken(qywxParam.getEcEventToken());
        }
        if(null!=qywxParam&&!StringUtils.isEmpty(qywxParam.getEcEventAesKey()))
        {
            redisConfigStorage.setEcEventAesKey(qywxParam.getEcEventAesKey());
        }
        if(null!=qywxParam&&!StringUtils.isEmpty(qywxParam.getMpAppId()))
        {
            redisConfigStorage.setMpAppId(qywxParam.getMpAppId());
        }
        if(null!=qywxParam&&!StringUtils.isEmpty(qywxParam.getEnableWelcome()))
        {
            redisConfigStorage.setEnableWelcome(qywxParam.getEnableWelcome());
        }
        this.redisConfigStorage=redisConfigStorage;
    }

    @Override
    public RedisConfigStorageImpl getRedisConfigStorage() {
        return redisConfigStorage;
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
    public synchronized void  updateCorpInfo(String corpId, String secret,String agentId) {
        //更新到数据库
        paramMapper.updateCorpInfo(corpId,secret,agentId);
        //更新到redis
        redisConfigStorage.setCorpId(corpId);
        redisConfigStorage.setSecret(secret);
        redisConfigStorage.setAgentId(agentId);

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

    @Override
    public String getEnableWelcome() {
        String enableWelcome=this.redisConfigStorage.getEnableWelcome();

        if(StringUtils.isEmpty(enableWelcome))
        {
            QywxParam qywxParam=paramMapper.getQywxParam();
            enableWelcome=qywxParam==null?"":qywxParam.getEnableWelcome();
            if(!StringUtils.isEmpty(enableWelcome)) {
                this.redisConfigStorage.setEnableWelcome(enableWelcome);
            }
        }
        return this.redisConfigStorage.getEnableWelcome();
    }

    @Override
    public synchronized void setEnableWelcome(String enableWelcome) {
         //更新到数据库
        paramMapper.updateEnableWelcome(enableWelcome);
        //更新到redis
        this.redisConfigStorage.setEnableWelcome(enableWelcome);
    }

    @Override
    public String getMpAppId() {
        String mpAppId=this.redisConfigStorage.getMpAppId();

        if(StringUtils.isEmpty(mpAppId))
        {
            QywxParam qywxParam=paramMapper.getQywxParam();
            mpAppId=qywxParam==null?"":qywxParam.getMpAppId();
            if(!StringUtils.isEmpty(mpAppId)) {
                this.redisConfigStorage.setMpAppId(mpAppId);
            }
        }
        return this.redisConfigStorage.getMpAppId();
    }

    @Override
    public synchronized void setMpAppId(String mpAppId) {
        //更新到数据库
        paramMapper.updateMpAppId(mpAppId);
        //更新到redis
        this.redisConfigStorage.setMpAppId(mpAppId);
    }

    @Override
    public String getCorpId() {
        String corpId=this.redisConfigStorage.getCorpId();

        if(StringUtils.isEmpty(corpId))
        {
            QywxParam qywxParam=paramMapper.getQywxParam();
            corpId=qywxParam==null?"":qywxParam.getCorpId();
            if(!StringUtils.isEmpty(corpId)) {
                this.redisConfigStorage.setCorpId(corpId);
            }
        }
        return this.redisConfigStorage.getCorpId();
    }

    @Override
    public String getAgentId() {
        String agentId=this.redisConfigStorage.getAgentId();

        if(StringUtils.isEmpty(agentId))
        {
            QywxParam qywxParam=paramMapper.getQywxParam();
            agentId=qywxParam==null?"":qywxParam.getAgentId();
            if(!StringUtils.isEmpty(agentId)) {
                this.redisConfigStorage.setAgentId(agentId);
            }
        }
        return this.redisConfigStorage.getAgentId();
    }

    @Override
    public String getJsapiTicket() throws WxErrorException{
        String jsApiTicket=this.redisConfigStorage.getJsapiTicket();

        if(StringUtils.isEmpty(jsApiTicket))
        {
            log.debug("开始获取jsApiTicket");
            if(StringUtils.isEmpty(this.redisConfigStorage.getCorpId()))
            {
                throw new WxErrorException(WxError.builder().errorCode(-999).errorMsg("尚未配置企业微信公司ID").build());
            }
            String requestUrl= WxPathConsts.GET_JSAPI_TICKET+"?access_token="+this.getAccessToken();
            String resultContent=OkHttpUtil.getRequest(redisConfigStorage.getApiUrl(requestUrl));

            log.debug("获取jsApiTicket的返回结果为:{}",resultContent);
            JSONObject jsonObject = JSON.parseObject(resultContent);
            WxError error = WxError.fromJsonObject(jsonObject);
            if (error.getErrorCode() != 0) {
                throw new WxErrorException(error);
            }

            jsApiTicket = jsonObject.getString("ticket");
            Integer expiresIn = jsonObject.getInteger("expires_in");
            this.redisConfigStorage.setJsapiTicket(jsApiTicket, expiresIn);
        }
        return this.redisConfigStorage.getJsapiTicket();
    }

    @Override
    public String getAgentJsapiTicket() throws WxErrorException{
        String agentJsapiTicket=this.redisConfigStorage.getAgentJsapiTicket();

        if(StringUtils.isEmpty(agentJsapiTicket))
        {
            log.debug("开始获取agentJsapiTicket");
            if(StringUtils.isEmpty(this.redisConfigStorage.getCorpId()))
            {
                throw new WxErrorException(WxError.builder().errorCode(-999).errorMsg("尚未配置企业微信公司ID").build());
            }
            String requestUrl= WxPathConsts.GET_AGENT_CONFIG_TICKET+"&access_token="+this.getAccessToken();
            String resultContent=OkHttpUtil.getRequest(redisConfigStorage.getApiUrl(requestUrl));

            log.debug("获取jsApiTicket的返回结果为:{}",resultContent);
            JSONObject jsonObject = JSON.parseObject(resultContent);
            WxError error = WxError.fromJsonObject(jsonObject);
            if (error.getErrorCode() != 0) {
                throw new WxErrorException(error);
            }

            agentJsapiTicket = jsonObject.getString("ticket");
            Integer expiresIn = jsonObject.getInteger("expires_in");
            this.redisConfigStorage.setAgentJsapiTicket(agentJsapiTicket, expiresIn);
        }
        return this.redisConfigStorage.getAgentJsapiTicket();
    }

    @Override
    public void saveFile(String title, String content) {
        paramMapper.saveFile(title,content);
    }

    @Override
    public QywxParam getFileMessage() {
        return paramMapper.getFileMessage();
    }

}
