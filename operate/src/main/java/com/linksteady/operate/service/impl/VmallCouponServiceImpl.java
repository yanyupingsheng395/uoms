package com.linksteady.operate.service.impl;

import com.linksteady.operate.dao.CouponMapper;
import com.linksteady.operate.dao.VmallCouponMapper;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.PushProperties;
import com.linksteady.operate.service.CouPonService;
import com.linksteady.operate.service.VmallCouPonService;
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
public class VmallCouponServiceImpl implements VmallCouPonService {

    @Autowired
    private VmallCouponMapper couponMapper;

    @Autowired
    private PushProperties pushProperties;

    @Override
    public List<CouponInfo> getList(int limit, int offset) {
        return couponMapper.getList(limit, offset);
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
        validCoupon();
    }

    @Override
    public List<CouponInfo> getCouponList(String groupId, String userValue, String lifeCycle, String pathActive) {
        List<CouponInfo> couponList = couponMapper.getCouponList(groupId);
        validCoupon();
        return couponList;
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
        if(dataList.size() > 0) {
            insertData.stream().filter(x->{
                AtomicBoolean res = new AtomicBoolean(false);
                dataList.forEach(y->{
                    if(y.getCouponDenom().equals(x.getCouponDenom()) && y.getCouponThreshold().equals(x.getCouponThreshold())) {
                        res.set(true);
                    }
                });
                return res.get();
            }).collect(Collectors.toList());
            if(insertData.size() > 0) {
                couponMapper.insertCalculatedCoupon(insertData);
            }
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
        String couponSendType = pushProperties.getCouponSendType();
        if(couponSendType == null) {
            throw new RuntimeException("系统发送补贴的方式未在配置表中配置！");
        }
        //设置所有券为验证通过
        couponMapper.validCouponPass();
        // 补贴有效截止日期未配置为空
        couponMapper.validEndDateNull();
        //补贴有效日期已失效
        couponMapper.validEndDateNotNull();
    }

    @Override
    public List<CouponInfo> getIntelCouponList() {
        return couponMapper.getIntelCouponList();
    }
}