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

    List<DailyHead> getPageList(int start, int end, String touchDt);

    int getTotalCount(String touchDt);

    void updateStatus(String headId, String status);

    DailyHead getDailyHeadById(String headId);

    /**
     * 获取每日成长任务的效果统计
     * @param id
     * @return
     */
    DailyHead getEffectById(String id);

    Map<String, Object> getCurrentAndTaskDate(String headId);

    /**
     * 获取推送数据
     * @param headId
     * @return
     */
    Map<String, Object> getPushData(String headId);

    List<DailyGroupTemplate> getUserGroupList();

    void setSmsCode(String groupId, String smsCode);

    List<DailyPersonal> getDailyPersonalEffect(DailyPersonalVo dailyPersonalVo, int start, int end, String headId);

    int getDailyPersonalEffectCount(DailyPersonalVo dailyPersonalVo, String headId);

    boolean validUserGroup();

    /**
     * 更新头表的操作时间戳
     * @param headId
     * @param opChangeDate
     */
    void updateHeaderOpChangeDate(String headId, Long opChangeDate);

    /**
     * 获取生成文案的锁  true表示加锁成功 false表示加锁失败
     */
    boolean getTransContentLock(String headId);

    /**
     * 释放生成文案的锁
     */
    void delTransLock();

    /**
     * 验证操作时间戳
     */
    int validateOpChangeTime(String headId,Long opChangeDate);

    /**
     * 获取预览用户的统计数据
     */
    List<DailyUserStats> getUserStats(String headerId);

    List<DailyUserStats> getUserStatsBySpu(String headerId,String userValue,String pathActive,String lifecycle);

    List<DailyUserStats> getUserStatsByProd(String headerId,String userValue,String pathActive,String lifecycle,String spuName);
}
