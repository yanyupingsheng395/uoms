package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.CouponMapper;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.CouponInfo;
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
        couponMapper.update(couponInfo);
        // 更改成长组的检查状态
        if(couponInfo.getValidStatus().equalsIgnoreCase("N")) {
            Integer couponId = couponInfo.getCouponId();
            dailyMapper.updateGroupCheckFlagByCouponId(String.valueOf(couponId), "N");
        }
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
}
