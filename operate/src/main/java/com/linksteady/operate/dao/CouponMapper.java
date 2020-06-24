package com.linksteady.operate.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.operate.domain.CouponInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CouponMapper extends MyMapper<CouponInfo> {

    /**
     * 获取所有有效的优惠券列表
     */
    List<CouponInfo>  selectAllCouponList();

    /**
     * 获取优惠券的列表
     * @param limit
     * @param offset
     * @return
     */
    List<CouponInfo> getList(@Param("limit") int limit, @Param("offset") int offset);

    int getTotalCount();

    /**
     * 获取优惠券的数量
     * @param groupId
     * @return
     */
    List<Integer> getCouponIdsByGroupId(String groupId);

    /**
     * 新增补贴
     * @param couponInfo
     */
    void save(CouponInfo couponInfo);

    /**
     * 更新补贴
     * @param couponInfo
     */
    void update(CouponInfo couponInfo);

    /**
     * 根据优惠券ID获取优惠券信息
     * @param couponId
     * @return
     */
    CouponInfo getByCouponId(String couponId);

    /**
     * 优惠券是否已经被使用 返回已经被引用的优惠券的ID列表
     * @param couponIds
     * @return
     */
    List<String> isCouponUsed(@Param("couponIds") List<String> couponIds);

    /**
     * 删除优惠券
     * @param couponId
     */
    void deleteCoupon(@Param("couponId") String couponId);

    /**
     * 根据优惠券状态为无效
     * @param couponId
     */
    void updateCouponInvalid(@Param("couponId") String couponId);

    /**
     * 判断优惠券历史上时候被每日运营使用过
     * @param couponId
     * @return
     */
    int isUsedHistory(@Param("couponId") String couponId);

    /**
     * 写入从智能补贴过来的优惠券信息
     * @param couponInfoList
     */
    void insertCalculatedCoupon(List<CouponInfo> couponInfoList);

    /**
     * 删除优惠券群组关系
     * @param groupIds
     */
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

    /**
     * 删除智能优惠券快照表的数据
     */
    void deleteLaseAisnpData();

    /**
     * 智能优惠券快照表写入数据
     */
    void insertNewData();

    /**
     * 获取有效的优惠券条数
     * @return
     */
    int getValidCoupon();

    /**
     * 获取无效的优惠券条数
     */
    int getInvalidCoupon();

    void updateDiscountLevel();

    void deleteAllCouponGroupData();

    void resetCouponGroupData();

    List<CouponInfo> getCouponListByGroup(String userValue, String lifeCycle, String pathActive, String tarType);
}