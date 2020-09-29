package com.linksteady.operate.dao;

import com.linksteady.operate.domain.SendCouponRecord;
import com.linksteady.operate.vo.CouponInfoVO;
import com.linksteady.operate.vo.SendCouponVO;

import java.util.List;

/**
 * @author huang
 * @date 2019-07-31
 */
public interface QywxSendCouponMapper {

    /**
     * 获取当前头信息下的优惠券列表
     */
    List<CouponInfoVO> getCouponList(long headId);

    /**
     * 查询当前优惠券有多少人
     */
    int getCouponUserCount(long headId,long couponId);

    /**
     * 获取待发优惠券的人员列表
     */
    List<SendCouponVO> getCouponUserList(long headId, long couponId, int limit, int offset);


    /**
     * 保存发券记录
     */
    void saveSendCouponRecord(SendCouponRecord sendCouponRecord);

    /**
     * 更新发券信息
     */
    void updateCouponSendRecord(String sendResult,long sendRecordId,List<SendCouponVO> sendCouponVOList);

    void updateSendCouponFlag(long headId);

}
