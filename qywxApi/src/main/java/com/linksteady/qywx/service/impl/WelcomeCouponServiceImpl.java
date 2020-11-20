package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.WelcomeCouponMapper;
import com.linksteady.qywx.domain.QywxWelcomeCoupon;
import com.linksteady.qywx.service.WelcomeCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/8
 */
@Service
public class WelcomeCouponServiceImpl implements WelcomeCouponService {

    @Autowired
    private WelcomeCouponMapper welcomeCouponMapper;

    @Override
    public int getTableDataCount() {
        return welcomeCouponMapper.getTableDataCount();
    }

    @Override
    public List<QywxWelcomeCoupon> getTableDataList(Integer limit, Integer offset) {
        return welcomeCouponMapper.getTableDataList(limit, offset);
    }

    @Override
    public void saveData(QywxWelcomeCoupon qywxWelcomeCoupon) {
        welcomeCouponMapper.saveData(qywxWelcomeCoupon);
    }

    @Override
    public QywxWelcomeCoupon getDataById(String couponId) {
        List<QywxWelcomeCoupon> data = welcomeCouponMapper.getDataById(couponId);
        return data.size() > 0 ? data.get(0):null;
    }

    @Override
    public void updateData(QywxWelcomeCoupon qywxWelcomeCoupon) {
        welcomeCouponMapper.updateData(qywxWelcomeCoupon);
    }

    @Override
    public void deleteCouponById(String couponId) {
        welcomeCouponMapper.deleteCouponById(couponId);
    }
}
