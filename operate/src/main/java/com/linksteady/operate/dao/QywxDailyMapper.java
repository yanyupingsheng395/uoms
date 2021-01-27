package com.linksteady.operate.dao;

import com.linksteady.operate.domain.*;
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
}
