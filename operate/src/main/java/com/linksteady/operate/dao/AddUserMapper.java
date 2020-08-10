package com.linksteady.operate.dao;

import com.linksteady.common.domain.Ztree;
import com.linksteady.operate.domain.AddUserConfig;
import com.linksteady.operate.domain.AddUserHead;
import com.linksteady.operate.domain.AddUserSchedule;
import com.linksteady.operate.domain.QywxParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;

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

    void editConfig(AddUserConfig addUserConfig);

    List<Map<String, String>> getSource();

    /**
     * 写入待推送的用户明细
     */
    void insertAddUserList(@Param("headId") long headId, @Param("whereInfo")String whereInfo);

    /**
     * 判断当前任务是否存在推送明细
     */
    int getAddUserListCount(@Param("headId") long headId);

    /**
     * 获取企业微信默认的参数
     */
    QywxParam getQywxParam();

    /**
     * 更新拉新任务的推送节奏数据
     * totalNum 总人数
     * defaultAddcount 默认每日添加人数
     * defaultApplyRate 默认转化率
     * dailyAddNum 预计每日添加人数
     * waitDays 预计推送完需要多少天
     * addTotal 预计添加总人数
     */
    void updatePushParameter(
            long headId,
            int totalNum,
            int defaultAddCount,
            double defaultApplyRate,
            int dailyAddNum,
            int waitDays,
            int addTotal);

    /**
     * 更新head表的version字段
     */
    int updateHeadVersion(long headId,int version);

    /**
     * 获取当前天是否存在执行中的计划
     */
    int getRunningSchedule(long currentDay);

    /**
     * 保存当前的推送计划
     */
    void saveAddUserSchedule(AddUserSchedule addUserSchedule);

    /**
     * 更新推送明细
     */
    int updateAddUserList(long headId,long scheduleId,long targetNum);

    /**
     * 更新主记录上的剩余人数
     */
    void updateHeadWaitUserCnt(long headId, long afterWaitNum,long afterFinishNum,String opUserName);

    /**
     * 更新推送内容到推送计划表
     */
    void pushToPushListLarge(long headId, long scheduleId,long scheduleDate);

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

    void updateSmsContentAndContactWay(String headId, String smsContent, String contactWayId, String contactWayUrl);

    List<Map<String, Object>> getSendAndApplyData(String headId);

    List<Map<String, Object>> getStatisApplyData(String headId, String scheduleId);
}


