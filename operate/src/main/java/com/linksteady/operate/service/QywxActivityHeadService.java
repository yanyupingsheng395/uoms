package com.linksteady.operate.service;

import com.linksteady.operate.domain.*;
import com.linksteady.operate.vo.FollowUserVO;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface QywxActivityHeadService {

    /**
     * 获取头表的分页数据
     * @param limit
     * @param offset
     * @param name
     * @return
     */
    List<ActivityHead> getDataListOfPage(int limit, int offset, String name, String date, String status);
    /**
     * 获取数据记录总数
     * @param name
     * @return
     */
    int getDataCount(String name, String date, String status);

    /**
     * 插入头表
     * @param activityHead
     * @return
     */
    int saveActivityHead(ActivityHead activityHead, String coupons);

    /**
     * 根据ID获取
     * @param headId
     * @return
     */
    ActivityHead findById(Long headId);

    /**
     * 通过headId获取活动名称
     * @param headId
     * @return
     */
    String getActivityName(String headId);

    /**
     * 通过状态判断是否可以执行计划
     * @param id
     * @return
     */
    int getActivityStatus(String id);

    void deleteData(Long headId);

    int getDeleteCount(Long headId);

    /**
     * 更新头表的状态
     * @param headId
     * @param status
     * @param type
     */
    void updateStatus(Long headId, String status, String type);

    /**
     * 活动头表失效
     */
    void expireActivityHead();

    List<ActivityCoupon> findCouponList(Long headId);
    /**
     * 获取企业微信成员列表
     *
     * @return
     */
    List<FollowUserVO> getAllFollowUserList(Long headId);

    /**
     * 根据headID,获取ActivityHead表数据
     * @param headId
     * @return
     */
    ActivityHead getActivityHeadEffectById(Long headId);


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

    /**
     *
     * @param headId
     * @param followUserId
     * @return
     */
    QywxActivityStaffEffect getActivityStaffEffect(Long headId, String followUserId);
}
