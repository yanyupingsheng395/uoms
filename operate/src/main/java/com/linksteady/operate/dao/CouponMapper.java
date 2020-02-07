package com.linksteady.operate.dao;

import com.linksteady.operate.config.MyMapper;
import com.linksteady.operate.domain.CouponInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CouponMapper extends MyMapper<CouponInfo> {

    List<CouponInfo> getList(@Param("startRow") int startRow, @Param("endRow") int endRow, @Param("validStatus") String validStatus);

    int getTotalCount(@Param("validStatus") String validStatus);

    List<Integer> getCouponIdsByGroupId(String groupId);

    void deleteByGroupId(String groupId);

    void insertByGroupId(String groupId, List<String> couponIds);

    void save(CouponInfo couponInfo);

    void update(CouponInfo couponInfo);

    CouponInfo getByCouponId(String couponId);

    List<String> isCouponUsed(@Param("couponIds") List<String> couponIds);

    void updateStatus(String couponId);

    void deleteCoupon(@Param("ids") List<String> ids);

    List<CouponInfo> getCouponList(String groupId);

    void insertCalculatedCoupon();

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
     * 验证链接是否配置
     */
    void validCouponUrl();

    /**
     * 验证通过
     */
    void validCouponPass();

    /**
     * 获取配置到成长组中的所有优惠券信息
     */
    List<Map<String,Object>> selectGroupCouponInfo();
}