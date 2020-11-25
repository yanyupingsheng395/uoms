package com.linksteady.operate.dao;

import com.linksteady.operate.domain.*;
import com.linksteady.operate.vo.QywxScheduleEffectVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/7/16
 */
public interface AddUserTriggerMapper {

    int getHeadCount();

    List<AddUserHead> getHeadPageList(int limit, int offset);

    void saveHeadData(AddUserHead addUserHead);

    void deleteHead(String id);

    AddUserHead getHeadById(long id);

    List<Map<String, String>> getSource();

    /**
     * 判断当前任务是否存在推送明细
     */
    int getAddUserListCount(@Param("headId") long headId);

    /**
     * 更新head表的version字段
     */
    int updateHeadVersion(long headId, int version);

    /**
     * 获取是否存在执行中的计划
     */
    int getRunningScheduleCount();

    /**
     * 获取正在执行中的计划
     */
    AddUserSchedule getRunningSchedule();

    /**
     * 保存当前的推送计划
     */
    void saveAddUserSchedule(AddUserSchedule addUserSchedule);

    /**
     * 更新主记录状态为执行中
     */
    void updateHeadToDoing(long headId, String opUserName);

    void updateSmsContentAndContactWay(String headId, String smsContent, String contactWayId, String contactWayUrl, String isSourceName, String isProdName);

    List<Map<String, Object>> getSendAndApplyData(String headId);

    List<Map<String, Object>> getStatisApplyData(String headId, String scheduleId);

    List<Map<Long, Object>> geRegionData();

    /**
     * 查看任务的执行计划数
     */
    int getScheduleCount(long headId);

    String getStatus(String headId);

    /**---------------------------------下面的方法是调度中用的-----------------------------------*/
    /**
     * 更新推送结果(实际状态) [调度方法]
     */
    void updatePushResult();

    /**
     * 更新执行计划为完成 [调度方法]
     */
    void updateScheduleToDone();


    /**
     * 更新头数据的状态为停止 （执行中的任务，且不存在尚在执行中的计划） [调度方法]
     */
    void updateHeadToStop();

    /**
     * 更新计划表中的实际推送成功人数、通过申请人数 [调度方法]
     */
    void calculateScheduleEffect();

    /**
     * 更新头表的状态 [调度方法]
     */
    void calculateHeadEffect();

    /**
     * 删除最近10天的schedule的效果数据
     */
    void deleteScheduleEffect();

    /**
     * 获取最近10日内的schedule列表
     */
    List<AddUserSchedule> getLastScheduleList();

    /**
     * 查询某个schedule的转化数据
     */
    List<QywxScheduleEffectVO> getScheduleCovStatis(long scheduleId);

    /**
     * 保存逐日转化率数据
     */
    void saveScheduleEffect(List<AddUserEffect> addUserEffectList);

    /**
     * 更新推送列表中是否已加企业微信的标志信息  (如果一个手机号在企业微信外部联系人中匹配多个人，则只取按添加时间最近的一条)
     */
    void updateQywxAddInfo();

    /**
     * 获取待处理的的下单的用户( 如果一个人在此时间段内下多段，则只取商品价格最高的那条)
     */
    List<AddUserTriggerQueue> getOrders(LocalDateTime orderDt);

    /**
     * 将待处理的用户写入排队表中
     */
    void addToTriggerQueue(List<AddUserTriggerQueue> orderUserVOList);

    /**
     * 获取本批次要处理的最大的queueId
     */
    Map<String,Long> getTriggerQueueInfo(long slotsNum);

    /**
     * 删除排队表中已处理完的数据
     */
    void deleteTriggerQueue(long queueId);

    /**
     * 更新计划表中的剩余数量、实际推送数量
     * costSlots 表示本次推送被消耗掉的数量
     * actualApplyNum 表次本次推送实际推送的数量
     */
    void updateScheduleRemainUserCnt(long scheduleId,long costSlots,long actualApplyNum);

    /**
     * 分页获取排队表中的数据
     */
    List<AddUserTriggerQueue> getTriggerQueueData(long queueId,int limit,int offset);

    /**
     * 写入推送明细表
     */
    void insertAddUserList(List<AddUser> addUserList);

    /**
     * 写推送内容到推送计划表
     */
    void pushToPushListLarge(List<AddUser> adduserList, long scheduleDate);

    /**
     * 更新头表的发送申请总数量
     */
    void updateHeadApplyUserCnt(long headId,long applyUserCnt);

    /**
     *根据活吗ID获取渠道活吗数据
     */
    QywxContactWay getContactWayById(Long contactWayId);
}


