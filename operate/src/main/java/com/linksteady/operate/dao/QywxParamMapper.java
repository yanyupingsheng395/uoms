package com.linksteady.operate.dao;

import com.linksteady.operate.domain.QywxParam;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author huangkun
 * @date 2020-07-18
 */
public interface QywxParamMapper {

    QywxParam getQywxParam();

    void updateQywxParam(int dailyAddNum,double dailyAddRate,int applyNum,String opUser);

    void updateTriggerNum(int triggerNum,String opUser);

    int updateVersion(int version);

    int updateOrderSyncTimes(LocalDateTime orderSyncDt, long orderSyncTimes);

}
