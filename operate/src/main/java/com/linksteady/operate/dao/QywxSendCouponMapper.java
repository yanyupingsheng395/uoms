package com.linksteady.operate.dao;

import com.linksteady.operate.domain.CouponInfo;
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

    /**
     * 获取券的有效结束时间
     * @param couponIdentity
     */
    String getCouponEndDate(String couponIdentity);

    int getCouponSn();

    void updateCouponSn(int lastCouponSn);

    /**
     * 根据couponId去查优惠券的信息
     * @param couponId
     * @return
     */
    CouponInfoVO queryCoupon(Long couponId);

    /**
     * 获取没有使用过的优惠券列表
     * @param couponId
     * @param count
     * @return
     */
    List<String> getCouponSnList(long couponId, int count);

    /**
     *
     * @param couponSnList
     * @param couponIdentity
     */
    void updateCouponSnList(List<String> couponSnList, String couponIdentity,long couponId);

    /**
     * 文件上传优惠券数据
     * @param mobiles
     * @param couponId
     */
    void uploadCoupon(List<String> mobiles, Long couponId);
}
