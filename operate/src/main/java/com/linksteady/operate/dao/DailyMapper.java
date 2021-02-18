package com.linksteady.operate.dao;

import com.linksteady.operate.domain.*;
import com.linksteady.operate.vo.DailyPersonalVO;
import com.linksteady.operate.vo.DailyUserStatsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyMapper {

    List<DailyHead> getPageList(int limit, int offset, String touchDt);

    int getTotalCount(@Param("touchDt") String touchDt);

    int updateStatus(@Param("headId") long headId, @Param("status") String status,@Param("version") int version);

    void updateActualNum(String headId, int num);

    DailyHead getDailyHeadById(Long headId);

    DailyHead getEffectById(Long id);

    /**
     * 获取推送结果数据图
     * @param headId
     * @return
     */
    List<DailyStatis> getDailyStatisList(Long headId);

    /**
     * 个体结果只获取已经转化的结果
     * @param dailyPersonalVo
     * @param headId
     * @return
     */
    List<DailyPersonalEffect> getDailyPersonalEffect(DailyPersonalVO dailyPersonalVo, int limit, int offset, String headId);

    /**
     * 个体结果只获取已经转化的结果
     * @param dailyPersonalVo
     * @param headId
     * @return
     */
    int getDailyPersonalEffectCount(DailyPersonalVO dailyPersonalVo, String headId);

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
     * 按成长类型统计人数
     * @param headId
     */
    List<DailyUserStatsVO> getTargetInfoByGrowthType(Long headId);

    /**
     * 按成长类型[序列]统计人数
     * @param headId
     */
    List<DailyUserStatsVO> getTargetInfoByGrowthSeriesType(Long headId);


    /**
     * 按品类统计推荐人数
     * @param headId
     */
    List<DailyUserStatsVO> getTargetInfoBySpu(Long headId);

    /**
     * 按商品统计推荐人数
     * @param headId
     */
    List<DailyUserStatsVO> getTargetInfoByProd(Long headId, String spuName);

    /**
     * 按用户价值统计
     */
    List<DailyUserStatsVO> getTargetInfoByUserValue(Long headId);

    /**
     * 特定用户价值下，按用户活跃度、生命周期的统计
     */
    List<DailyUserStatsVO> getTargetInfoMatrix(Long headId, String userValue);

    /**
     * 按优惠券来进行统计
     */
    List<DailyUserStatsVO> getTargetInfoByCoupon(Long headId);

    String getTodayStatus();


    void expireDailyHead();

    String getLifeCycleByUserId(String userId, String headId);


}
