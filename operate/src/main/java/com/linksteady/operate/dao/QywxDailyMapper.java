package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyHead;
import com.linksteady.operate.domain.QywxDailyHeader;
import com.linksteady.operate.vo.QywxUserStats;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author huang
 * @date 2020-05-12
 */
public interface QywxDailyMapper {

    /**
     * 获取微信任务的分页记录
     * @param limit
     * @param offset
     * @param taskDate
     * @return
     */
    List<QywxDailyHeader> getHeadList(int limit, int offset, String taskDate);

    /**
     * 获取总记录数
     * @param taskDate
     * @return
     */
    int getTotalCount(@Param("taskDate") String taskDate);

    QywxDailyHeader getHeadInfo(long HeadId);

    /**
     * 按成长类型统计人数
     * @param headId
     */
    List<QywxUserStats> getTargetInfoByGrowthType(Long headId);

    /**
     * 按成长类型[序列]统计人数
     * @param headId
     */
    List<QywxUserStats> getTargetInfoByGrowthSeriesType(Long headId);


    /**
     * 按品类统计推荐人数
     * @param headId
     */
    List<QywxUserStats> getTargetInfoBySpu(Long headId);

    /**
     * 按商品统计推荐人数
     * @param headId
     */
    List<QywxUserStats> getTargetInfoByProd(Long headId,String spuName);

    /**
     * 按用户价值统计
     */
    List<QywxUserStats> getTargetInfoByUserValue(Long headId);

    /**
     * 特定用户价值下，按用户活跃度、生命周期的统计
     */
    List<QywxUserStats> getTargetInfoMatrix(Long headId,String userValue);

    /**
     * 按企业微信成员统计
     */
    List<QywxUserStats> getTargetInfoByUser(Long headId);

    /**
     * 按优惠券来进行统计
     */
    List<QywxUserStats> getTargetInfoByCoupon(Long headId);

    int updateStatus(@Param("headId") long headId, @Param("status") String status,@Param("version") int version);


    /**
     * 更新头表的操作时间戳、推送时段、推送方式
     * @param headId
     */
    void updateHeaderPushInfo(@Param("headId") long headId,
                              @Param("pushMethod") String pushMethod,
                              @Param("pushPeriod") String pushPeriod,
                              @Param("effectDays") Long effectDays);


}
