package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyHead;
import com.linksteady.operate.domain.DailyPersonal;
import com.linksteady.operate.vo.DailyPersonalVO;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyService {

    List<DailyHead> getPageList(int limit, int offset, String touchDt);

    int getTotalCount(String touchDt);

    DailyHead getDailyHeadById(Long headId);

    /**
     * 获取每日成长任务的效果统计
     * @param id
     * @return
     */
    DailyHead getEffectById(Long id);

    /**
     * 获取推送数据
     * @param headId
     * @return
     */
    Map<String, Object> getPushData(Long headId);


    List<DailyPersonal> getDailyPersonalEffect(DailyPersonalVO dailyPersonalVo, int limit, int offset, String headId);

    int getDailyPersonalEffectCount(DailyPersonalVO dailyPersonalVo, String headId);

    /**
     * 获取生成文案的锁  true表示加锁成功 false表示加锁失败
     */
    boolean getTransContentLock(String headId);

    /**
     * 释放生成文案的锁
     */
    void delTransLock();

    Map<String, Object> getSelectedUserGroup(String groupId);

    int getSmsIsCoupon(String smsCode, String is_coupon);

    int getValidDailyHead();

//    void updateSmsCodeNull(String smsCode);

    Map<String, Object> getUserStatsData(Long headId);

    Map<String, Object> getProdCountBySpu(Long headId,String spuName);

    Map<String, Object> getMatrixData(Long headId,String userValue);

    void pushContent(DailyHead dailyHead,String pushMethod,String pushPeriod,Long effectDays) throws Exception;

    /**
     * 每日运营任务失效
     */
    void expireDailyHead();

    String getLifeCycleByUserId(String userId, String headId);
}
