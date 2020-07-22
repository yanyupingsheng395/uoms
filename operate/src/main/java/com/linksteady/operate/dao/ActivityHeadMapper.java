package com.linksteady.operate.dao;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ActivityCoupon;
import com.linksteady.operate.domain.ActivityHead;
import com.linksteady.operate.domain.ActivityPlan;
import com.linksteady.operate.domain.ActivityTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface ActivityHeadMapper {

    List<ActivityHead> getDataListOfPage(int limit, int offset, String name, String date, String status);

    int getDataCount(@Param("name") String name);

    void saveActivityHead(ActivityHead activityHead);

    void updateActiveHead(ActivityHead activityHead);

    ActivityHead findById(Long headId);

    String getActivityName(String headId);

    int getActivityStatus(String id);

    String getPreheatStatus(String headId);

    Map<String, String> getDataChangedStatus(Long headId, String stage);

    /**
     * 更新头表预售状态
     * @param headId
     * @param planType 计划类型(通知 or 正式)
     */
    void updatePreheatStatusHead(Long headId,String status,String planType);

    /**
     * 更新头表正式状态
     * @param headId
     * @param planType 计划类型(通知 or 正式)
     */
    void updateFormalStatusHead(Long headId,String status,String planType);

    void deleteActivity(Long headId);

    int getDeleteCount(Long headId);

    /**
     * 失效预售通知
     */
    void expirePreheatNotify();

    /**
     * 失效预售正式
     */
    void expirePreheatDuring();

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
}
