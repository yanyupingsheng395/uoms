package com.linksteady.operate.dao;

import com.linksteady.operate.domain.*;
import com.linksteady.operate.vo.CouponInfoVO;
import com.linksteady.operate.vo.SendCouponVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author huang
 * @date 2020-05-12
 */
public interface QywxDailyMapper {

    /**
     * 获取微信任务的分页记录
     * @param limit
     * @param offset
     * @param taskDate
     * @return
     */
    List<QywxDailyHeader> getHeadList(int limit, int offset, String taskDate);

    /**
     * 获取总记录数
     * @param taskDate
     * @return
     */
    int getTotalCount(@Param("taskDate") String taskDate);

    QywxDailyHeader getHeadInfo(long HeadId);

    int updateStatus(@Param("headId") long headId, @Param("status") String status,@Param("effectDays") long effectDays,@Param("version") int version);

    void insertPushList(QywxPushList qywxPushList);

    void updatePushList(@Param("pushId") Long pushId,String status,String msgId,String faildList,String remark);

    /**
     * 查询企业微信消息推送错误的条数
     */
    int getPushErrorCount(long headId);

    void updateStatusToDoneCouponError(long headId);

    void updateStatusToDonePushError(long headId);

    void updateSendCouponFlag(long headId);

    /**
     * 获取推送结果数据图
     * @param headId
     * @return
     */
    List<QywxDailyStatis> getQywxDailyStatisList(Long headId);

    List<QywxDailyPersonalEffect> getConvertDetailData(int limit, int offset, long headId);

    int getConvertDetailCount(long headId);

    void expireActivityDailyHead();

    int updateVersion(Long headId, int version);

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
    List<SendCouponVO> getCouponUserList(long headId, long couponId, int limit, int offset,String identityColumn);

    /**
     * 更新每日运营明细的发券信息
     */
    void updateCouponSendInfo(long sendRecordId,List<SendCouponVO> sendCouponVOList);
}
