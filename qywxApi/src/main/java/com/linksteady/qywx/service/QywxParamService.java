package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.QywxParam;

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
    QywxParam updateQywxParam(int dailyAddNum, double dailyAddRate, String opUser, int version) throws Exception;

    /**
     * 更新企业微信的参数 (主动触达的人数)
     */
    QywxParam updateTriggerNum(int triggerNum, String opUser, int version) throws Exception;

}
