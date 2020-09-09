package com.linksteady.operate.config;

import com.linksteady.common.dao.ConfigMapper;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.domain.HeartBeatInfo;
import com.linksteady.common.domain.enums.ConfigEnum;
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
            configMapper.updateConfig(ConfigEnum.pushFlag.getKeyCode(),"Y");
            //重新加载配置到redis
            configService.loadConfigToRedis();
            //发送命令到远端
            heartBeatInfo.setSignal(signal);
            redisMessageService.sendPushSingal(heartBeatInfo);

        }else if(signal.getSignalCode().equals(PushSignalEnum.SIGNAL_STOP.getSignalCode()))
        {
            //更新数据库
            configMapper.updateConfig(ConfigEnum.pushFlag.getKeyCode(),"N");
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
        return Integer.parseInt(configService.getValueByName(ConfigEnum.repeatPushDays.getKeyCode()));
    }

    public String getPushFlag() {
        return configService.getValueByName(ConfigEnum.pushFlag.getKeyCode());
    }

    public String getPushMethod() {
        return configService.getValueByName(ConfigEnum.pushMethod.getKeyCode());
    }

    public String getIsTestEnv() {
        return configService.getValueByName(ConfigEnum.isTestEnv.getKeyCode());
    }

    public String getProductDetailUrl()
    {
        return configService.getValueByName(ConfigEnum.productDetailUrl.getKeyCode());
    }

    public int getShortUrlLen() {
        return Integer.parseInt(configService.getValueByName(ConfigEnum.shortUrlLen.getKeyCode()));
    }

    public int getProdNameLen() {
        return Integer.parseInt(configService.getValueByName(ConfigEnum.prodNameLen.getKeyCode()));
    }

    public int getSourceNameLen() {
        return Integer.parseInt(configService.getValueByName(ConfigEnum.sourceNameLen.getKeyCode()));
    }

    public String getCouponSendType() {
        return configService.getValueByName(ConfigEnum.couponSendType.getKeyCode());
    }

    public int getCouponNameLen() {
        return Integer.parseInt(configService.getValueByName(ConfigEnum.couponNameLen.getKeyCode()));
    }

    public int getPriceLen() {
        return Integer.parseInt(configService.getValueByName(ConfigEnum.priceLen.getKeyCode()));
    }

    public int getProfitLen() {
        return Integer.parseInt(configService.getValueByName(ConfigEnum.profitLen.getKeyCode()));
    }

    public String getPushVendor() {
        return configService.getValueByName(ConfigEnum.pushVendor.getKeyCode());
    }

    public String getOpenNightSleep() {
        return configService.getValueByName(ConfigEnum.openNightSleep.getKeyCode());
    }

    public int getNightStart() {
        return Integer.parseInt(configService.getValueByName(ConfigEnum.nightStart.getKeyCode()));
    }

    public int getNightEnd() {
        return Integer.parseInt(configService.getValueByName(ConfigEnum.nightEnd.getKeyCode()));
    }

    public String getOpenCallback() {
        return configService.getValueByName(ConfigEnum.openCallback.getKeyCode());
    }

    public String getClAccount() {
        return configService.getValueByName(ConfigEnum.clAccount.getKeyCode());
    }

    public String getClPassword() {
        return configService.getValueByName(ConfigEnum.clPassword.getKeyCode());
    }

    public String getClRequestServerUrl() {
        return configService.getValueByName(ConfigEnum.clRequestServerUrl.getKeyCode());
    }

    public String getClPullMoUrl() {
        return configService.getValueByName(ConfigEnum.clPullMoUrl.getKeyCode());
    }

    public String getClReportRequestUrl() {
        return configService.getValueByName(ConfigEnum.clReportRequestUrl.getKeyCode());
    }

    public String getMontnetsAccount() {
        return configService.getValueByName(ConfigEnum.montnetsAccount.getKeyCode());
    }

    public String getMontnetsPassword() {
        return configService.getValueByName(ConfigEnum.montnetsPassword.getKeyCode());
    }

    public String getMontnetsMasterIpAddress() {
        return configService.getValueByName(ConfigEnum.montnetsMasterIpAddress.getKeyCode());
    }

    public long getDailyPollingMins() {
        return Long.parseLong(configService.getValueByName(ConfigEnum.dailyPollingMins.getKeyCode()));
    }

    public long getBatchPollingMins() {
        return Long.parseLong(configService.getValueByName(ConfigEnum.batchPollingMins.getKeyCode()));
    }

    public long getMoPollingMins() {
        return Long.parseLong(configService.getValueByName(ConfigEnum.moPollingMins.getKeyCode()));
    }

    public long getRptPollingMins() {
        return Long.parseLong(configService.getValueByName(ConfigEnum.rptPollingMins.getKeyCode()));
    }

    /**
     * 签名
     */
    public String getSignature() {
        return  configService.getValueByName(ConfigEnum.signature.getKeyCode());
    }

    /**
     * 退订信息
     * @return
     */
    public String getUnsubscribe() {
        return   configService.getValueByName(ConfigEnum.unsubscribe.getKeyCode());
    }

    /**
     * 实际发送短信时是否需要签名信息
     * @return
     */
    public String getSendSignatureFlag() {
        return  configService.getValueByName(ConfigEnum.sendSignatureFlag.getKeyCode());
    }

    /**
     * 编辑短信时是否需要退订信息
     * @return
     */
    public String getSendUnsubscribeFlag() {
        return  configService.getValueByName(ConfigEnum.sendUnsubscribeFlag.getKeyCode());
    }

    /**
     * 商品详情页短链是否可用的标记 Y表示可用 N表示不可用
     * @return
     */
    public String getProdUrlEnabled() {
        return  configService.getValueByName(ConfigEnum.prodUrlEnabled.getKeyCode());
    }

    public String getUrl() {
        return  configService.getValueByName(ConfigEnum.url.getKeyCode());
    }

    public String getProdName() {
        return  configService.getValueByName(ConfigEnum.prodName.getKeyCode());
    }

    public String getCouponName() {
        return  configService.getValueByName(ConfigEnum.couponName.getKeyCode());
    }

    public String getPrice() {
        return  configService.getValueByName(ConfigEnum.price.getKeyCode());
    }

    public String getProfit() {
        return  configService.getValueByName(ConfigEnum.profit.getKeyCode());
    }

    public String getSoureName() {
        return  configService.getValueByName(ConfigEnum.sourceName.getKeyCode());
    }

    public String getSmsEnabled() {
        return  configService.getValueByName(ConfigEnum.smsEnabled.getKeyCode());
    }

    public String getQywxEnabled() {
        return  configService.getValueByName(ConfigEnum.qywxEnabled.getKeyCode());
    }

    public String getWxofficialEnabled() {
        return  configService.getValueByName(ConfigEnum.wxofficialEnabled.getKeyCode());
    }
}
