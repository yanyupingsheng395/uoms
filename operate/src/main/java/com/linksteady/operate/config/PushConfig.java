package com.linksteady.operate.config;

import com.linksteady.common.dao.ConfigMapper;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.domain.HeartBeatInfo;
import com.linksteady.operate.domain.enums.PushPropertiesEnum;
import com.linksteady.operate.domain.enums.PushSignalEnum;
import com.linksteady.operate.service.RedisMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PushConfig {

    @Autowired
    ConfigService configService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private RedisMessageService redisMessageService;

    @Transactional(rollbackFor = Exception.class)
    public synchronized void sendPushSignal(PushSignalEnum signal,String currentUser) throws Exception{
        HeartBeatInfo heartBeatInfo =HeartBeatInfo.getInstance();
        //启动
        if(signal.getSignalCode().equals(PushSignalEnum.SIGNAL_START.getSignalCode()))
        {
            //更新数据库
            configMapper.updateConfig(PushPropertiesEnum.pushFlag.getKeyCode(),"Y");
            //重新加载配置到redis
            configService.loadConfigToRedis();
            //发送命令到远端
            heartBeatInfo.setSignal(signal);
            redisMessageService.sendPushSingal(heartBeatInfo);

        }else if(signal.getSignalCode().equals(PushSignalEnum.SIGNAL_STOP.getSignalCode()))
        {
            //更新数据库
            configMapper.updateConfig(PushPropertiesEnum.pushFlag.getKeyCode(),"N");
            //重新加载配置到redis
            configService.loadConfigToRedis();
            //发送命令到远端
            heartBeatInfo.setSignal(signal);
            redisMessageService.sendPushSingal(heartBeatInfo);
        }else if(signal.getSignalCode().equals(PushSignalEnum.SIGNAL_PRINT.getSignalCode()))
        {
            //打印
            //发送命令到远端
            heartBeatInfo.setSignal(signal);
            redisMessageService.sendPushSingal(heartBeatInfo);
        }else if(signal.getSignalCode().equals(PushSignalEnum.SIGNAL_REFRESH.getSignalCode()))
        {
            configService.loadConfigToRedis();
            //通知推送端也重新加载配置
            heartBeatInfo.setSignal(signal);
            redisMessageService.sendPushSingal(heartBeatInfo);
        }else
        {
            //什么也不做
        }
    }


    public int getRepeatPushDays() {
        return Integer.parseInt(configService.getValueByName(PushPropertiesEnum.repeatPushDays.getKeyCode()));
    }

    public String getPushFlag() {
        return configService.getValueByName(PushPropertiesEnum.pushFlag.getKeyCode());
    }

    public String getPushMethod() {
        return configService.getValueByName(PushPropertiesEnum.pushMethod.getKeyCode());
    }

    public int getSmsLengthLimit() {
        return Integer.parseInt(configService.getValueByName(PushPropertiesEnum.smsLengthLimit.getKeyCode()));
    }

    public String getProductUrl() {
        return configService.getValueByName(PushPropertiesEnum.productUrl.getKeyCode());
    }

    public String getIsTestEnv() {
        return configService.getValueByName(PushPropertiesEnum.isTestEnv.getKeyCode());
    }

    public String getDemoShortUrl() {
        return configService.getValueByName(PushPropertiesEnum.demoShortUrl.getKeyCode());
    }

    public int getShortUrlLen() {
        return Integer.parseInt(configService.getValueByName(PushPropertiesEnum.shortUrlLen.getKeyCode()));
    }

    public int getProdNameLen() {
        return Integer.parseInt(configService.getValueByName(PushPropertiesEnum.prodNameLen.getKeyCode()));
    }

    public String getCouponSendType() {
        return configService.getValueByName(PushPropertiesEnum.couponSendType.getKeyCode());
    }

    public int getCouponNameLen() {
        return Integer.parseInt(configService.getValueByName(PushPropertiesEnum.couponNameLen.getKeyCode()));
    }

    public int getPriceLen() {
        return Integer.parseInt(configService.getValueByName(PushPropertiesEnum.priceLen.getKeyCode()));
    }

    public int getProfitLen() {
        return Integer.parseInt(configService.getValueByName(PushPropertiesEnum.profitLen.getKeyCode()));
    }

    public String getPushVendor() {
        return configService.getValueByName(PushPropertiesEnum.pushVendor.getKeyCode());
    }

    public String getOpenNightSleep() {
        return configService.getValueByName(PushPropertiesEnum.openNightSleep.getKeyCode());
    }

    public int getNightStart() {
        return Integer.parseInt(configService.getValueByName(PushPropertiesEnum.nightStart.getKeyCode()));
    }

    public int getNightEnd() {
        return Integer.parseInt(configService.getValueByName(PushPropertiesEnum.nightEnd.getKeyCode()));
    }

    public String getOpenCallback() {
        return configService.getValueByName(PushPropertiesEnum.openCallback.getKeyCode());
    }

    public String getClAccount() {
        return configService.getValueByName(PushPropertiesEnum.clAccount.getKeyCode());
    }

    public String getClPassword() {
        return configService.getValueByName(PushPropertiesEnum.clPassword.getKeyCode());
    }

    public String getClRequestServerUrl() {
        return configService.getValueByName(PushPropertiesEnum.clRequestServerUrl.getKeyCode());
    }

    public String getClPullMoUrl() {
        return configService.getValueByName(PushPropertiesEnum.clPullMoUrl.getKeyCode());
    }

    public String getClReportRequestUrl() {
        return configService.getValueByName(PushPropertiesEnum.clReportRequestUrl.getKeyCode());
    }

    public String getMontnetsAccount() {
        return configService.getValueByName(PushPropertiesEnum.montnetsAccount.getKeyCode());
    }

    public String getMontnetsPassword() {
        return configService.getValueByName(PushPropertiesEnum.montnetsPassword.getKeyCode());
    }

    public String getMontnetsMasterIpAddress() {
        return configService.getValueByName(PushPropertiesEnum.montnetsMasterIpAddress.getKeyCode());
    }

    public long getDailyPollingMins() {
        return Long.parseLong(configService.getValueByName(PushPropertiesEnum.dailyPollingMins.getKeyCode()));
    }

    public long getBatchPollingMins() {
        return Long.parseLong(configService.getValueByName(PushPropertiesEnum.batchPollingMins.getKeyCode()));
    }

    public long getMoPollingMins() {
        return Long.parseLong(configService.getValueByName(PushPropertiesEnum.moPollingMins.getKeyCode()));
    }

    public long getRptPollingMins() {
        return Long.parseLong(configService.getValueByName(PushPropertiesEnum.rptPollingMins.getKeyCode()));
    }

    /**
     * 签名
     */
    public String getSignature() {
        return  configService.getValueByName(PushPropertiesEnum.signature.getKeyCode());
    }

    public String getSignatureFlag() {
        return  configService.getValueByName(PushPropertiesEnum.signatureFlag.getKeyCode());
    }

    public String getUnsubscribe() {
        return   configService.getValueByName(PushPropertiesEnum.unsubscribe.getKeyCode());
    }

    public String getUnsubscribeFlag() {
        return  configService.getValueByName(PushPropertiesEnum.unsubscribeFlag.getKeyCode());
    }
}
