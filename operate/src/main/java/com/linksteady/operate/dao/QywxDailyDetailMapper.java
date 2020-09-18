package com.linksteady.operate.dao;

import com.linksteady.operate.domain.QywxDailyDetail;
import com.linksteady.operate.domain.QywxDailyStaffEffect;
import com.linksteady.operate.vo.FollowUserVO;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface QywxDailyDetailMapper {

    /**
     * 获取当前header_id下的用户列表
     * @param headId
     * @return
     */
    int getUserCount(@Param("headId") Long headId);

    /**
     * 分页获取当header_id下选中的用户名单
     * @param headId
     * @param limit
     * @param offset
     * @return
     */
    List<QywxDailyDetail> getUserList(@Param("headId") Long headId, @Param("limit") int limit, @Param("offset") int offset);

    /**
     * 保存推送的文案信息到临时表
     */
    void insertPushContentTemp(@Param("list") List<QywxDailyDetail> list);

    /**
     * 删除文案临时表中的数据
     */
    void deleteQywxPushContentTemp(@Param("headId") Long headId);

    /**
     * 将文案内容从临时表更新到每日运营明细表
     */
    void updatePushContentFromTemp(@Param("headId") Long headId);

    /**
     * 获取当前header_id下的用户列表
     * @param headId
     * @return
     */
    int getQywxDetailCount(@Param("headId") Long headId,@Param("followUserId") String followUserId);

    /**
     * 分页获取当header_id下选中的用户名单
     * @param headId
     * @param limit
     * @param offset
     * @return
     */
    List<QywxDailyDetail> getQywxDetailList(@Param("headId") Long headId,
                                            @Param("limit") int limit,
                                            @Param("offset") int offset,
                                            @Param("followUserId") String followUserId);

    /**
     * 获取当前header_id下转化的用户列表
     * @param headId
     * @return
     */
    int getConversionCount(@Param("headId") Long headId,@Param("followUserId") String followUserId);

    /**
     * 分页获取当header_id下转化的用户明细
     * @param headId
     * @param limit
     * @param offset
     * @return
     */
    List<QywxDailyDetail> getConversionList(@Param("headId") Long headId,
                                            @Param("limit") int limit,
                                            @Param("offset") int offset,
                                            @Param("followUserId") String followUserId);

    List<FollowUserVO> getFollowUserList(Long headId);


    List<Map<String,String>> getTestPushData();

    QywxDailyStaffEffect getDailyStaffEffect(Long headId, String followUserId);

    List<QywxDailyDetail> getQywxUserListByHeadId(Long headId);
}
