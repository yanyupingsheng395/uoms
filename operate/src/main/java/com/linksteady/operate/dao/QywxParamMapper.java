package com.linksteady.operate.dao;

import com.linksteady.operate.domain.QywxParam;
import com.linksteady.operate.vo.AddUserHistoryVO;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author huangkun
 * @date 2020-07-18
 */
public interface QywxParamMapper {

    QywxParam getQywxParam();

    void updateQywxParam(int dailyAddNum,double dailyAddRate,int applyNum,String opUser);

    void updateTriggerNum(int triggerNum,String opUser);

    int updateVersion(int version);

    int updateOrderSyncTimes(LocalDateTime orderSyncDt);

    /**
     * 往拉新历史表里写入数据
     */
    void insertAddUserHistory(String phone_num);

    /**
     * 删除企业微信拉新历史(超过多少天的被删除)
     */
    void deleteAddUserHistory(int diffDays);

    /**
     * 获取推送历史
     */
    List<AddUserHistoryVO> getAddUserHistory(int diffDays);

    /**
     * 批量写入推送历史
     */
    void insertAddUserListHistory(List<String> phonenumList);

}
