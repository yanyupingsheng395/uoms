package com.linksteady.operate.service;

import com.linksteady.operate.domain.QywxWelcomeCoupon;
import com.linksteady.operate.domain.QywxWelcomeProduct;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/8
 */
public interface QywxWelcomeCouponService {

    int getTableDataCount();

    List<QywxWelcomeCoupon> getTableDataList(Integer limit, Integer offset);

    void saveData(QywxWelcomeCoupon qywxWelcomeCoupon);

    QywxWelcomeCoupon getDataById(String couponId);

    void updateData(QywxWelcomeCoupon qywxWelcomeCoupon);

    void deleteCouponById(String couponId);
}
