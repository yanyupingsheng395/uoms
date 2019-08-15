package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyPushInfo;
import com.linksteady.operate.domain.DailyPushQuery;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

public interface DailyPushMapper {

    /**
     * 获取当前header_id下选中的用户数量
     * @param headId
     * @return
     */
    int getUserCount(@Param("headId") String headId);

    /**
     * 分页获取当header_id下选中的用户名单
     * @param headId
     * @param start
     * @param end
     * @return
     */
    List<DailyPushQuery> getUserList(@Param("headId") String headId,@Param("start") int start,@Param("end") int end);

    /**
     * 保存推送的文案信息
     */
    void updatePushContent(@Param("list") List<DailyPushInfo> list);

    /**
     * 获得当前要推送的最大的daily_detail_id
     * @return
     */
    int getPrePushUserMaxId();

    /**
     * 获得当前要推送的消息数量
     * @return
     */
    int getPrePushUserCount(int dailyDetailId);

    /**
     * 获得当前要推送的消息列表(分页)
     * @return
     */
    List<DailyPushInfo> getPrePushUserList(@Param("dailyDetailId") int dailyDetailId,@Param("start") int start,@Param("end") int end);

    /**
     * 更新已推送的消息的状态
     * @param list
     * @param status
     */
    void updateSendStatus(@Param("list") List<DailyPushInfo> list, @Param("status") String status);

    /**
     * 更新日运营头信息状态为 推送结束、效果统计中 (当前为doing 推送中 且 明细表中push_status没有为P状态的，将其status更新为done 推送结束、效果统计中)
     */
    void updateHeaderToDone();

    /**
     * 更新头表中推送状态的统计信息
     */
    void updateHeaderSendStatis();

    /**
     * 更新统计表中的触达统计信息
     */
    void updatePushStatInfo();

}
