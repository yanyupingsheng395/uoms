package com.linksteady.operate.dao;

import com.linksteady.operate.domain.QywxDailyHeader;
import com.linksteady.operate.domain.QywxMsgResult;
import com.linksteady.operate.domain.QywxPushList;
import com.linksteady.operate.vo.CouponInfoVO;
import com.linksteady.operate.vo.QywxUserStatsVO;
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

    List<String> getPushMsgIdList();

    void deletePushResult(String msgId);

    void saveMsgResult(List<QywxMsgResult> qywxMsgResultList);

    void updateExecStatus();
}
