package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.CouponInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface VmallCouponMapper extends MyMapper<CouponInfo> {

    List<CouponInfo> getList(@Param("limit") int limit, @Param("offset") int offset);

    int getTotalCount();

    List<Integer> getCouponIdsByGroupId(String groupId);

    void deleteByGroupId(String groupId);

    void insertByGroupId(String groupId, List<String> couponIds);

    void save(CouponInfo couponInfo);

    void update(CouponInfo couponInfo);

    CouponInfo getByCouponId(String couponId);

    List<String> isCouponUsed(@Param("couponIds") List<String> couponIds);

    void deleteCoupon(@Param("couponId") String couponId);

    void updateCouponInvalid(@Param("couponId") String couponId);

    int isUsedHistory(@Param("couponId") String couponId);

    List<CouponInfo> getCouponList(String groupId);

    void insertCalculatedCoupon(List<CouponInfo> couponInfoList);

    void deleteCouponGroup(@Param("groupIds") List<String> groupIds);

    int checkCouponName(@Param("couponName") String couponName);

    /**
     * 验证日期为空
     */
    void validEndDateNull();

    /**
     * 验证日期失效
     */
    void validEndDateNotNull();

    /**
     * 验证通过
     */
    void validCouponPass();

    /**
     * 获取配置到成长组中的所有优惠券信息
     */
    List<Map<String,Object>> selectGroupCouponInfo();

    /**
     * 获取系统中所有智能券
     * @return
     */
    List<CouponInfo> getSysCoupon();

    /**
     * 获取所有优惠券的列表(有效)
     * @return
     */
    List<CouponInfo> getIntelCoupon();

    List<CouponInfo> getIntelCouponList();

    void deleteLaseAisnpData();

    void insertNewData();

    int getValidCoupon();

    void updateDiscountLevel();

    void deleteAllCouponGroupData();

    void resetCouponGroupData();

    List<CouponInfo> getCouponListByGroup(String userValue, String lifeCycle, String pathActive, String tarType);
}