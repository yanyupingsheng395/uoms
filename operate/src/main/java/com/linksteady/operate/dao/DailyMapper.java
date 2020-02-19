package com.linksteady.operate.dao;

import com.linksteady.operate.domain.*;
import com.linksteady.operate.vo.DailyPersonalVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyMapper {

    List<DailyHead> getPageList(int start, int end, String touchDt);

    List<DailyHead> getTouchPageList(int start, int end, String touchDt);

    int getTotalCount(@Param("touchDt") String touchDt);

    int getTouchTotalCount(@Param("touchDt") String touchDt);

    void updateStatus(@Param("headId") String headId, @Param("status") String status);

    void updateActualNum(String headId, int num);

    DailyHead getDailyHeadById(String headId);

    String getTouchDt(String headId);

    Map<String, Object> getTaskInfo(String headId);

    Map<String, Object> getTotalNum(String headId);

    List<DailyGroupTemplate> getUserGroupList(@Param("activeList") List<String> activeList);

    void setSmsCode(List<String> groupIds, String smsCode);

    /**
     * 更新日运营头信息状态为 完成 (当前为done执行中 且 明细表中push_status没有为P状态的，将其status更新为finish 结束)
     */
    void updateHeaderToFinish();

    /**
     * 更新头表中推送状态的统计信息
     */
    void updateHeaderSendStatis();

    int validUserGroup();

    DailyHead getEffectById(String id);

    /**
     * 获取推送结果数据图
     * @param headId
     * @return
     */
    List<DailyStatis> getDailyStatisList(String headId);

    /**
     * 个体结果只获取已经转化的结果
     * @param dailyPersonalVo
     * @param start
     * @param end
     * @param headId
     * @return
     */
    List<DailyPersonal> getDailyPersonalEffect(DailyPersonalVo dailyPersonalVo, int start, int end, String headId);

    /**
     * 个体结果只获取已经转化的结果
     * @param dailyPersonalVo
     * @param headId
     * @return
     */
    int getDailyPersonalEffectCount(DailyPersonalVo dailyPersonalVo, String headId);

    /**
     * 通过couponId更改群组的检查状态
     * @param couponId
     */
    void updateGroupCheckFlagByCouponId(@Param("couponId") String couponId, @Param("checkFlag") String checkFlag);





    /**
     * 更新头表的操作时间戳
     * @param headId
     * @param opChangeDate
     */
    void updateHeaderOpChangeDate(@Param("headId") String headId, @Param("opChangeDate") Long opChangeDate);

    /**
     * 验证操作时间戳
     */
    int validateOpChangeTime(@Param("headId") String headId, @Param("opChangeDate") Long opChangeDate);

    /**
     * 获取预览用户统计数据
     */
    List<DailyUserStats> getUserStats(@Param("headId") String headId);

    List<DailyUserStats> getUserStatsBySpu(@Param("headId") String headId, @Param("userValue") String userValue, @Param("pathActive") String pathActive, @Param("lifecycle") String lifecycle);

    List<DailyUserStats> getUserStatsByProd(@Param("headId") String headId, @Param("userValue") String userValue, @Param("pathActive") String pathActive, @Param("lifecycle") String lifecycle, @Param("spuName") String spuName);



    String getTodayStatus();

    Map<String, Object> getSelectedUserGroup(String groupId);

    int getSmsIsCoupon(String smsCode, String isCoupon);

    int getValidDailyHead();

    void updateSmsCodeNull(String smsCode);
}
