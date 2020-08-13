package com.linksteady.operate.dao;

import com.linksteady.operate.domain.QywxParam;

/**
 * @author huangkun
 * @date 2020-07-18
 */
public interface QywxParamMapper {

    QywxParam getQywxParam();

    void updateQywxParam(int dailyAddNum,double dailyAddRate,int applyNum,String opUser);

    void updateTriggerNum(int triggerNum,String opUser);

    int updateVersion(int version);

}
