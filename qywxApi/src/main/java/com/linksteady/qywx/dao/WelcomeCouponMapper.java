package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxWelcomeCoupon;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/8
 */
public interface WelcomeCouponMapper {

    int getTableDataCount();

    List<QywxWelcomeCoupon> getTableDataList(Integer limit, Integer offset);

    void saveData(QywxWelcomeCoupon qywxWelcomeCoupon);

    List<QywxWelcomeCoupon> getDataById(String couponId);

    void updateData(QywxWelcomeCoupon qywxWelcomeCoupon);

    void deleteCouponById(String couponId);
}
