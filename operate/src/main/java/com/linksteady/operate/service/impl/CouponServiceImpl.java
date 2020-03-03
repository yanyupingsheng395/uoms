package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.CouponMapper;
import com.linksteady.operate.dao.DailyMapper;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.DailyProperties;
import com.linksteady.operate.service.CouPonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by hxcao on 2019-04-29
 */
@Service
public class CouponServiceImpl implements CouPonService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private DailyProperties dailyProperties;

    @Override
    public List<CouponInfo> getList(int startRow, int endRow) {
        return couponMapper.getList(startRow, endRow);
    }

    @Override
    public int getTotalCount() {
        return couponMapper.getTotalCount();
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
    public void deleteCoupon(List<String> ids) {
        for(String couponId:ids)
        {
            //判断券是否曾被历史引用过
            if(couponMapper.isUsedHistory(couponId)>0)
            {
                couponMapper.updateCouponInvalid(couponId);
            }else
            {
                couponMapper.deleteCoupon(couponId);
            }
        }

    }

    @Override
    public List<CouponInfo> getCouponList(String groupId, String userValue, String lifeCycle, String pathActive) {
        validCoupon();
        List<CouponInfo> couponList = couponMapper.getCouponList(groupId);
        if(StringUtils.isNotEmpty(userValue)) {
            couponList = couponList.stream().filter(x->x.getUserValue().contains(userValue)).collect(Collectors.toList());
        }
        if(StringUtils.isNotEmpty(lifeCycle)) {
            couponList = couponList.stream().filter(x->x.getLifeCycle().contains(lifeCycle)).collect(Collectors.toList());
        }
        if(StringUtils.isNotEmpty(pathActive)) {
            couponList = couponList.stream().filter(x->x.getPathActive().contains(pathActive)).collect(Collectors.toList());
        }
        return couponList;
    }

    /**
     * 获取智能补贴
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getCalculatedCoupon() {
        //获取系统计算出的优惠券列表
        List<CouponInfo> sysData = couponMapper.getSysCoupon();
        //获取所有有效的优惠券列表
        List<CouponInfo> couponInfoList = couponMapper.getIntelCoupon();
        //待插入的数据 如果有同一面额、同一门槛的已经存在，则忽略
        List<CouponInfo> insertData = sysData.stream().filter(s -> {
            long count = couponInfoList.stream().filter(c -> c.getCouponDenom().equals(s.getCouponDenom()) && c.getCouponThreshold().equals(s.getCouponThreshold())).count();
            return count == 0;
        }).collect(Collectors.toList());
        if(insertData.size() > 0) {
            couponMapper.insertCalculatedCoupon(insertData);
        }
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
