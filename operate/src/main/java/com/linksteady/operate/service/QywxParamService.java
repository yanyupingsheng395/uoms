package com.linksteady.operate.service;

import com.linksteady.operate.domain.QywxParam;

/**
 * @author huang
 * @date 2020-08-12
 */
public interface QywxParamService {

    /**
     * 获取企业微信的参数
     * @return
     */
    QywxParam getQywxParam();

    /**
     * 更新企业微信的参数 (每日加人上限、转化率)
     */
    void updateQywxParam(int dailyAddNum,double dailyAddRate,String opUser,int version) throws Exception;

    /**
     * 更新企业微信的参数 (主动触达的人数)
     */
    void updateTriggerNum(int triggerNum,String opUser,int version) throws Exception;

}
