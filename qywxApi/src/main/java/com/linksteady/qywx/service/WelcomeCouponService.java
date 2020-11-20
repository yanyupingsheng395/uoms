package com.linksteady.qywx.service;


import com.linksteady.qywx.domain.QywxWelcomeCoupon;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/8
 */
public interface WelcomeCouponService {

    int getTableDataCount();

    List<QywxWelcomeCoupon> getTableDataList(Integer limit, Integer offset);

    void saveData(QywxWelcomeCoupon qywxWelcomeCoupon);

    QywxWelcomeCoupon getDataById(String couponId);

    void updateData(QywxWelcomeCoupon qywxWelcomeCoupon);

    void deleteCouponById(String couponId);
}
