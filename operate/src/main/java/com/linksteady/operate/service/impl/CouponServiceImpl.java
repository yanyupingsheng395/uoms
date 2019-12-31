package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.CouponMapper;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.service.CouPonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
@Service
public class CouponServiceImpl implements CouPonService {

    @Autowired
    private CouponMapper couponMapper;

    @Override
    public List<CouponInfo> getList(int startRow, int endRow, String validStatus) {
        return couponMapper.getList(startRow, endRow, validStatus);
    }

    @Override
    public int getTotalCount(String validStatus) {
        return couponMapper.getTotalCount(validStatus);
    }

    @Override
    public List<Integer> getCouponIdsByGroupId(String groupId) {
        return couponMapper.getCouponIdsByGroupId(groupId);
    }

    @Override
    public void updateCouponId(String groupId, String couponId) {
        couponMapper.deleteByGroupId(groupId);
        List<String> couponIds = Arrays.asList(couponId.split(","));
        couponMapper.insertByGroupId(groupId, couponIds);
    }

    @Override
    public void save(CouponInfo couponInfo) {
        couponMapper.save(couponInfo);
    }

    @Override
    public void update(CouponInfo couponInfo) {
        couponMapper.update(couponInfo);
    }

    @Override
    public CouponInfo getByCouponId(String couponId) {
        return couponMapper.getByCouponId(couponId);
    }

    @Override
    public int isCouponUsed(String couponId) {
        return couponMapper.isCouponUsed(couponId);
    }

    @Override
    public void updateStatus(String couponId) {
        couponMapper.updateStatus(couponId);
    }

    @Override
    public void deleteCoupon(List<String> ids) {
        couponMapper.deleteCoupon(ids);
    }
}
