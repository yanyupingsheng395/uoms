package com.linksteady.operate.service;

import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.SmsTemplate;

import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
public interface CouPonService {

    List<CouponInfo> getList(int startRow, int endRow);

    int getTotalCount();

    List<Integer> getCouponIdsByGroupId(String groupId);

    void updateCouponId(String groupId, String couponId);

    void save(CouponInfo couponInfo);

    void update(CouponInfo couponInfo);

    CouponInfo getByCouponId(String couponId);

    /**
     * 判断优惠券是否被成长组引用
     * @param couponIds
     * @return
     */
    List<String> isCouponUsed(List<String> couponIds);

    void deleteCoupon(List<String> ids);

    List<CouponInfo> getCouponList(String groupId, String userValue, String lifeCycle, String pathActive);

    void getCalculatedCoupon(List<CouponInfo> dataList);

    void deleteCouponGroup(String groupId);

    int checkCouponName(String couponName);

    void validCoupon();

    List<CouponInfo> getIntelCouponList();
}
