package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.CouponMapper;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.service.CouPonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
@Service
public class CouponServiceImpl implements CouPonService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private DailyMapper dailyMapper;

    @Autowired
    private DailyProperties dailyProperties;

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
    @Transactional(rollbackFor = Exception.class)
    public void updateCouponId(String groupId, String couponId) {
        couponMapper.deleteByGroupId(groupId);
        List<String> couponIds = Arrays.asList(couponId.split(","));
        couponMapper.insertByGroupId(groupId, couponIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(CouponInfo couponInfo) {
        couponMapper.save(couponInfo);
    }

    /**
     * 对于失效的券的更改，含有券的成长组设置校验不通过
     * @param couponInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CouponInfo couponInfo) {
        // 验证信息
        validCoupon();
        couponMapper.update(couponInfo);
    }

    @Override
    public CouponInfo getByCouponId(String couponId) {
        return couponMapper.getByCouponId(couponId);
    }

    @Override
    public List<String> isCouponUsed(List<String> couponIds){
        return couponMapper.isCouponUsed(couponIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String couponId) {
        couponMapper.updateStatus(couponId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCoupon(List<String> ids) {
        couponMapper.deleteCoupon(ids);
    }

    @Override
    public List<CouponInfo> getCouponList(String groupId) {
        validCoupon();
        return couponMapper.getCouponList(groupId);
    }

    @Override
    public void getCalculatedCoupon() {
        couponMapper.insertCalculatedCoupon();
    }

    @Override
    public void deleteCouponGroup(String groupId) {
        couponMapper.deleteCouponGroup(Arrays.asList(groupId.split(",")));
    }

    @Override
    public int checkCouponName(String couponName) {
        return couponMapper.checkCouponName(couponName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void validCoupon() {
        String couponSendType = dailyProperties.getCouponSendType();
        if(couponSendType == null) {
            throw new RuntimeException("系统发送补贴的方式未在配置表中配置！");
        }
        couponMapper.validCouponPass();
        // 只需要验证有效期
        couponMapper.validEndDateNull();
        couponMapper.validEndDateNotNull();
        if("B".equalsIgnoreCase(couponSendType)) {
            couponMapper.validCouponUrl();
        }
    }
}
