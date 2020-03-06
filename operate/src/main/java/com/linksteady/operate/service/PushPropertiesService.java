package com.linksteady.operate.service;

import com.linksteady.common.domain.Tconfig;
import com.linksteady.operate.domain.enums.PushSignalEnum;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface PushPropertiesService {

    /**
     * 发送启动/停止/打印/更新配置 信号
     */
    void sendPushSignal(PushSignalEnum signal,String currentUser) throws Exception;

    /**
     * 重新加载配置
     * @param currentUser
     */
    void loadConfigToRedisAndRefreshProperties(String currentUser)  throws Exception;

    /**
     * 获取所有的推送配置信息
     */
    List<Tconfig> selectPushConfigList();

}
