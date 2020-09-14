package com.linksteady.operate.dao;

import com.linksteady.operate.domain.QywxDailyHeader;
import com.linksteady.operate.vo.QywxUserStatsVO;
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
    List<QywxUserStatsVO> getTargetInfoByGrowthType(Long headId);

    /**
     * 按成长类型[序列]统计人数
     * @param headId
     */
    List<QywxUserStatsVO> getTargetInfoByGrowthSeriesType(Long headId);


    /**
     * 按品类统计推荐人数
     * @param headId
     */
    List<QywxUserStatsVO> getTargetInfoBySpu(Long headId);

    /**
     * 按商品统计推荐人数
     * @param headId
     */
    List<QywxUserStatsVO> getTargetInfoByProd(Long headId, String spuName);

    /**
     * 按用户价值统计
     */
    List<QywxUserStatsVO> getTargetInfoByUserValue(Long headId);

    /**
     * 特定用户价值下，按用户活跃度、生命周期的统计
     */
    List<QywxUserStatsVO> getTargetInfoMatrix(Long headId, String userValue);

    /**
     * 按企业微信成员统计
     */
    List<QywxUserStatsVO> getTargetInfoByUser(Long headId);

    /**
     * 按优惠券来进行统计
     */
    List<QywxUserStatsVO> getTargetInfoByCoupon(Long headId);

    int updateStatus(@Param("headId") long headId, @Param("status") String status,@Param("version") int version);


    /**
     * 更新头表的操作时间戳
     * @param headId
     */
    void updateHeaderPushInfo(@Param("headId") long headId,
                              @Param("effectDays") Long effectDays);


}
