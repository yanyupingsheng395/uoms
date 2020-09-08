package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.QywxWelcomeCouponMapper;
import com.linksteady.operate.domain.QywxWelcomeCoupon;
import com.linksteady.operate.service.QywxWelcomeCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/8
 */
@Service
public class QywxWelcomeCouponServiceImpl implements QywxWelcomeCouponService {

    @Autowired
    private QywxWelcomeCouponMapper qywxWelcomeCouponMapper;

    @Override
    public int getTableDataCount() {
        return qywxWelcomeCouponMapper.getTableDataCount();
    }

    @Override
    public List<QywxWelcomeCoupon> getTableDataList(Integer limit, Integer offset) {
        return qywxWelcomeCouponMapper.getTableDataList(limit, offset);
    }

    @Override
    public void saveData(QywxWelcomeCoupon qywxWelcomeCoupon) {
        qywxWelcomeCouponMapper.saveData(qywxWelcomeCoupon);
    }

    @Override
    public QywxWelcomeCoupon getDataById(String couponId) {
        List<QywxWelcomeCoupon> data = qywxWelcomeCouponMapper.getDataById(couponId);
        return data.size() > 0 ? data.get(0):null;
    }

    @Override
    public void updateData(QywxWelcomeCoupon qywxWelcomeCoupon) {
        qywxWelcomeCouponMapper.updateData(qywxWelcomeCoupon);
    }

    @Override
    public void deleteCouponById(String couponId) {
        qywxWelcomeCouponMapper.deleteCouponById(couponId);
    }
}
