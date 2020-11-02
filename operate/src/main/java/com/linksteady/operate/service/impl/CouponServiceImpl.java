package com.linksteady.operate.service.impl;

import com.google.common.collect.HashBasedTable;
import com.linksteady.operate.config.PushConfig;
import com.linksteady.operate.dao.CouponMapper;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Created by hxcao on 2019-04-29
 */
@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private PushConfig pushConfig;

    @Override
    public List<CouponInfo> selectAllCouponList() {
        return couponMapper.selectAllCouponList();
    }

    @Override
    public List<CouponInfo> getList(int limit, int offset) {
        return couponMapper.getList(limit, offset);
    }

    @Override
    public int getTotalCount() {
        return couponMapper.getTotalCount();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(CouponInfo couponInfo) {
        couponMapper.save(couponInfo);
        validCoupon();
    }

    /**
     * 对于失效的券的更改，含有券的成长组设置校验不通过
     * @param couponInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CouponInfo couponInfo) {
        couponMapper.update(couponInfo);
        validCoupon();
    }

    @Override
    public CouponInfo getByCouponId(String couponId) {
        return couponMapper.getByCouponId(couponId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCoupon(List<String> ids) throws Exception{
        for(String couponId:ids) {
            //删除优惠券引用关系
            couponMapper.deleteCouponGroup(couponId);
            //删除优惠券(将状态标志置换为失效)
            couponMapper.updateCouponInvalid(couponId);
        }
        validCoupon();
    }

    /**
     * 获取智能补贴
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getCalculatedCoupon(List<CouponInfo> dataList) {
        // 清空快照表，插入新的快照数据
        couponMapper.deleteLaseAisnpData();
        couponMapper.insertNewData();

        //获取系统计算出的优惠券列表
        List<CouponInfo> sysData = couponMapper.getSysCoupon();
        //获取所有有效的优惠券列表
        List<CouponInfo> couponInfoList = couponMapper.getIntelCoupon();
        //待插入的数据 如果有同一面额、同一门槛的已经存在，则忽略
        List<CouponInfo> insertData = sysData.stream().filter(s -> {
            long count = couponInfoList.stream().filter(c -> c.getCouponDenom().equals(s.getCouponDenom()) && c.getCouponThreshold().equals(s.getCouponThreshold())).count();
            return count == 0;
        }).collect(Collectors.toList());

        List<CouponInfo> targetList=null;
        //如果用户选择了某些券，则只处理用户选择的券
        if(dataList.size() > 0) {
            HashBasedTable<Integer, Integer, String> userSelectCouponTable = HashBasedTable.create();
            dataList.forEach(i->{
                userSelectCouponTable.put(i.getCouponDenom(),i.getCouponThreshold(),"");
            });
            //只处理用户选择的数据
            targetList=insertData.stream().filter(x->userSelectCouponTable.contains(x.getCouponDenom(),x.getCouponThreshold())).collect(Collectors.toList());
        }else
        {
            targetList=insertData;
        }

        if(targetList.size() > 0) {
            couponMapper.insertCalculatedCoupon(targetList);
        }
    }

    @Override
    public int checkCouponName(String couponName) {
        return couponMapper.checkCouponName(couponName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void validCoupon() {
        String couponSendType = pushConfig.getCouponSendType();
        if(couponSendType == null) {
            throw new RuntimeException("补贴的方法方式未在系统中配置！");
        }
        //设置所有券为验证通过
        couponMapper.validCouponPass();
        // 补贴有效截止日期未配置为空
        couponMapper.validEndDateNull();
        //补贴有效日期已失效
        couponMapper.validEndDateNotNull();
        // 自行领取
        if(couponSendType.equalsIgnoreCase("A")) {
            //补贴链接为空
            couponMapper.validCouponUrl();
        }
    }

    @Override
    public List<CouponInfo> getIntelCouponList() {
        return couponMapper.getIntelCouponList();
    }
}
