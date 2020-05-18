package com.linksteady.operate.dao;

import com.linksteady.operate.domain.*;
import com.linksteady.operate.vo.DailyPersonalVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyMapper {

    List<DailyHead> getPageList(int limit, int offset, String touchDt);

    List<DailyHead> getTouchPageList(int limit, int offset, String touchDt);

    int getTotalCount(@Param("touchDt") String touchDt);

    int updateStatus(@Param("headId") long headId, @Param("status") String status,@Param("version") int version);

    void updateActualNum(String headId, int num);

    DailyHead getDailyHeadById(String headId);

    List<DailyGroupTemplate> getUserGroupList(@Param("activeList") List<String> activeList);

    void setSmsCode(List<String> groupIds, String smsCode);

    int validUserGroup();

    DailyHead getEffectById(String id);

    /**
     * 获取推送结果数据图
     * @param headId
     * @return
     */
    List<DailyStatis> getDailyStatisList(String headId);

    /**
     * 个体结果只获取已经转化的结果
     * @param dailyPersonalVo
     * @param headId
     * @return
     */
    List<DailyPersonal> getDailyPersonalEffect(DailyPersonalVo dailyPersonalVo, int limit, int offset, String headId);

    /**
     * 个体结果只获取已经转化的结果
     * @param dailyPersonalVo
     * @param headId
     * @return
     */
    int getDailyPersonalEffectCount(DailyPersonalVo dailyPersonalVo, String headId);

    /**
     * 通过couponId更改群组的检查状态
     * @param couponId
     */
    void updateGroupCheckFlagByCouponId(@Param("couponId") String couponId, @Param("checkFlag") String checkFlag);

    /**
     * 更新头表的操作时间戳、推送时段、推送方式
     * @param headId
     */
    void updateHeaderPushInfo(@Param("headId") long headId,
                              @Param("pushMethod") String pushMethod,
                              @Param("pushPeriod") String pushPeriod,
                              @Param("effectDays") Long effectDays);

    /**
     * 获取预览用户统计数据
     */
    List<DailyUserStats> getUserStats(@Param("headId") String headId);

    List<DailyUserStats> getUserStatsBySpu(@Param("headId") String headId, @Param("userValue") String userValue, @Param("pathActive") String pathActive, @Param("lifecycle") String lifecycle);

    List<DailyUserStats> getUserStatsByProd(@Param("headId") String headId, @Param("userValue") String userValue, @Param("pathActive") String pathActive, @Param("lifecycle") String lifecycle, @Param("spuName") String spuName);

    String getTodayStatus();

    Map<String, Object> getSelectedUserGroup(String groupId);

    int getSmsIsCoupon(String smsCode, String isCoupon);

    int getValidDailyHead();

    void updateSmsCodeNull(String smsCode);

    /**
     * 每日运营任务失效
     */
    void expireDailyHead();
}
