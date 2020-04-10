package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyGroupTemplate;
import com.linksteady.operate.domain.DailyHead;
import com.linksteady.operate.domain.DailyPersonal;
import com.linksteady.operate.domain.DailyUserStats;
import com.linksteady.operate.vo.DailyPersonalVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyService {

    List<DailyHead> getPageList(int limit, int offset, String touchDt);

    int getTotalCount(String touchDt);

    DailyHead getDailyHeadById(String headId);

    /**
     * 获取每日成长任务的效果统计
     * @param id
     * @return
     */
    DailyHead getEffectById(String id);

    /**
     * 获取推送数据
     * @param headId
     * @return
     */
    Map<String, Object> getPushData(String headId);

    List<DailyGroupTemplate> getUserGroupList();

    void setSmsCode(String groupId, String smsCode);

    List<DailyPersonal> getDailyPersonalEffect(DailyPersonalVo dailyPersonalVo, int limit, int offset, String headId);

    int getDailyPersonalEffectCount(DailyPersonalVo dailyPersonalVo, String headId);

    /**
     * 获取生成文案的锁  true表示加锁成功 false表示加锁失败
     */
    boolean getTransContentLock(String headId);

    /**
     * 释放生成文案的锁
     */
    void delTransLock();

    /**
     * 获取预览用户的统计数据
     */
    List<DailyUserStats> getUserStats(String headerId);

    List<DailyUserStats> getUserStatsBySpu(String headerId,String userValue,String pathActive,String lifecycle);

    List<DailyUserStats> getUserStatsByProd(String headerId,String userValue,String pathActive,String lifecycle,String spuName);

    Map<String, Object> getSelectedUserGroup(String groupId);

    int getSmsIsCoupon(String smsCode, String is_coupon);

    int getValidDailyHead();

    void updateSmsCodeNull(String smsCode);

    Map<String, Object> getUserStatsData(String headId);

    void pushContent(DailyHead dailyHead,String pushMethod,String pushPeriod,Long effectDays) throws Exception;

    String getTouchDt(String headId);

    /**
     * 每日运营任务失效
     */
    void expireDailyHead();
}
