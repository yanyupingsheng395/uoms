package com.linksteady.operate.dao;

import com.linksteady.operate.domain.*;
import com.linksteady.operate.vo.FollowUserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface QywxActivityHeadMapper {

    List<ActivityHead> getDataListOfPage(int limit, int offset, String name, String date, String status);

    int getDataCount(String name, String date, String status);

    void saveActivityHead(ActivityHead activityHead);

    void updateActiveHead(ActivityHead activityHead);

    ActivityHead findById(Long headId);

    String getActivityName(String headId);

    int getActivityStatus(String id);

    /**
     * 更新头表正式状态
     * @param headId
     * @param planType 计划类型(通知 or 正式)
     */
    void updateFormalStatusHead(Long headId, String status, String planType);

    void deleteActivity(Long headId);

    int getDeleteCount(Long headId);

    /**
     * 失效正式通知
     */
    void expireFormalNotify();

    /**
     * 失效正式正式
     */
    void expireFormalDuring();

    /**
     * 保存优惠券列表
     * @param couponList
     */
    void saveActivityCouponList(@Param("couponList") List<ActivityCoupon> couponList);

    /**
     * 获取优惠券列表
     * @param headId
     * @return
     */
    List<ActivityCoupon> getActivityCouponList(Long headId);

    /**
     * 获取成员列表
     * @param headId
     * @return
     */
    List<FollowUserVO> getAllFollowUserList(Long headId);

    /**
     *据headID,获取ActivityHead表数据
     * @param headId
     * @return
     */
    ActivityHead getActivityHeadById(Long headId);
    /**
     * 获取推送结果数据图
     * @param headId
     * @return
     */
    List<QywxActivityStatis> getQywxActivityStatisList(Long headId);
    /**
     *获取个体明细
     * @param limit
     * @param offset
     * @param headId
     * @return
     */
    List<QywxActivityConvertDetail> getConvertDetailData(int limit, int offset, Long headId);
    /**
     * 获取个体明细数量
     * @param headId
     * @return
     */
    int getConvertDetailCount(Long headId);

    QywxActivityStaffEffect getActivityStaffEffect(Long headId, String followUserId);
}
