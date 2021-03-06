package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/7/16
 */
public interface AddUserMapper {

    int getHeadCount();

    List<AddUserHead> getHeadPageList(int limit, int offset);

    void saveHeadData(AddUserHead addUserHead);

    void deleteHead(String id);

    AddUserHead getHeadById(long id);

    List<Map<String, String>> getSource();

    /**
     * 写入待推送的用户明细
     */
    void insertAddUserList(@Param("headId") long headId, @Param("whereInfo") String whereInfo);

    /**
     * 判断当前任务是否存在推送明细
     */
    int getAddUserListCount(@Param("headId") long headId);

    /**
     * 更新拉新任务的推送节奏数据
     * totalNum 总人数
     * activeNum 每日推送人数
     * defaultApplyRate 默认转化率
     * dailyAddNum 预计每日添加人数
     * waitDays 预计推送完需要多少天
     * addTotal 预计添加总人数
     */
    void updatePushParameter(
            long headId,
            int totalNum,
            int activeNum,
            double defaultApplyRate,
            int dailyAddNum,
            int waitDays,
            int addTotal);

    /**
     * 更新head表的version字段
     */
    int updateHeadVersion(long headId, int version);

    /**
     * 获取当前天是否存在执行中的计划
     */
    int getRunningSchedule(long currentDay);

    /**
     * 保存当前的推送计划
     */
    void saveAddUserSchedule(AddUserSchedule addUserSchedule);

    /**
     * 更新主记录上的剩余人数
     */
    void updateHeadWaitUserCnt(long headId, long afterWaitNum, long afterFinishNum, String opUserName);


    void updateSmsContentAndContactWay(String headId, String smsContent, String contactWayId, String contactWayUrl);

    List<Map<String, Object>> getSendAndApplyData(String headId);

    List<Map<String, Object>> getStatisApplyData(String headId, String scheduleId);

    List<Map<Long, Object>> geRegionData();

    /**
     * 查看任务的执行计划数
     */
    int getScheduleCount(long headId);

    /**
     * 获得未处理的用户列表
     */
    List<AddUser> getUnProcessAddUserList(long headId, long limit);

    /**
     * 更新推送明细上的scheduleId
     */
    int updateAddUserList(long headId, long scheduleId, List<AddUser> addUserList, String state, String smsContent);

    /**
     * 更新推送内容到推送计划表
     */
    void pushToPushListLarge(List<AddUser> addUserList, long scheduleDate);

    /**----------------------------------调度中方法 用于计算效果 -------------------------------*/
    /**
     * 更新推送结果
     */
    void updatePushResult();

    /**
     * 更新推送计划 （由进行中 更新为完成)
     */
    void updateScheduleToDone();

    /**
     * 更新头数据的状态为停止 （执行中的任务，且不存在尚在执行中的计划）
     */
    void updateHeadToStop();


    /**
     * 更新头数据的状态为结束 (执行中的任务，且不存在尚在执行中的计划 且剩余推送人数为0)
     */
    void updateHeadToDone();

    /**
     * 计算计划表中的效果字段
     */
    void calculateScheduleEffect();

    /**
     * 计算头表中的效果字段
     */
    void calculateHeadEffect();

    /**
     * 删除近10天的效果统计数据
     */
    void deleteScheduleEffect();

    /**
     * 获取近10天的计划列表
     */
    List<AddUserSchedule> getLastScheduleList();

    /**
     * 获取某个schedule下的效果统计数据
     * @param scheduleId
     * @return
     */
    List<QywxScheduleEffectVO> getScheduleCovStatis(long scheduleId);

    /**
     * 保存效果统计数据
     */
    void saveScheduleEffect(List<AddUserEffect> addUserEffectList);

}


