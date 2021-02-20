package com.linksteady.operate.dao;

import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.SendCouponRecord;
import com.linksteady.operate.vo.CouponInfoVO;
import com.linksteady.operate.vo.SendCouponVO;
import com.linksteady.operate.vo.couponSnCountVO;

import java.util.List;

/**
 * @author huang
 * @date 2019-07-31
 */
public interface QywxSendCouponMapper {

    /**
     * 保存发券记录
     */
    void saveSendCouponRecord(SendCouponRecord sendCouponRecord);

    int getCouponSn();

    void updateCouponSn(int lastCouponSn);


    /**
     * 获取没有使用过的优惠券列表
     * @param couponId
     * @param count
     * @return
     */
    List<String> getCouponSnList(long couponId, int count);

    /**
     * 获取每种优惠券的可用流水号数量
     */
    List<couponSnCountVO> getCouponSnCount();

    /**
     *
     * @param couponSnList
     */
    void updateCouponSnList(List<String> couponSnList,long couponId);

    /**
     * 文件上传优惠券数据
     * @param mobiles
     * @param couponId
     */
    void uploadCoupon(List<String> mobiles, Long couponId);
}
