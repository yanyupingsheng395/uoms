package com.linksteady.operate.service;

import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.SmsTemplate;

import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
public interface CouPonService {

    List<CouponInfo> getList(int startRow, int endRow, String validStatus);

    int getTotalCount(String validStatus);

    List<Integer> getCouponIdsByGroupId(String groupId);

    void updateCouponId(String groupId, String couponId);

    void save(CouponInfo couponInfo);

    void update(CouponInfo couponInfo);

    CouponInfo getByCouponId(String couponId);

    List<String> isCouponUsed(List<String> couponIds);

    void updateStatus(String couponId);

    void deleteCoupon(List<String> ids);

    List<CouponInfo> getCouponList(String groupId);

    void getCalculatedCoupon();

    void deleteCouponGroup(String groupId);

    int checkCouponName(String couponName);

    void validCoupon();

    List<String> getSelectedSmsCode(String groupId);
}
