package com.linksteady.operate.dao;
import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.vo.ActivityContentVO;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface ActivityDetailMapper {

    List<ActivityDetail> getPageList(int start, int end, String headId, Long planDtWid,String groupId);

    int getDataCount(String headId, Long planDtWid,String groupId);

    /**
     * 将文案内容写入临时表
     * @param targetList
     */
    void insertPushContentTemp(@Param("list") List<ActivityContentVO> targetList);


    /**
     * 将临时表的文案更新到正式表
     */
    void updatePushContentFromTemp(@Param("headId")  String headId);

    /**
     * 更新推送状态
     */
    void updatePushScheduleDate(@Param("headId")  String headId,@Param("planDateWid")  String planDateWid);

    /**
     * 删除文案临时表
     */
    void deleteContentTmp(@Param("headId")  String headId);

    /**
     * 查询出所有文案为空的记录的条数
     */
    int selectContentNulls(@Param("headId")  String headId,@Param("planDateWid")  String planDateWid);

    /**
     * 查询出所有文案长度超长的记录的条数
     */
    int selectContentLimit(@Param("headId")  String headId,@Param("planDateWid")  String planDateWid,@Param("limit") int limit);

    /**
     * 查询出所有文案含非法变量的记录的条数
     */
    int selectContentVariable(@Param("headId")  String headId,@Param("planDateWid")  String planDateWid);

    /**
     * 查询出所有推送时间为空的记录的条数
     */
    int selectPushScheduleNulls(@Param("headId")  String headId,@Param("planDateWid")  String planDateWid);

    /**
     * 查询出所有推送时间格式不正确的记录的条数
     */
    int selectPushScheduleInvalid(@Param("headId")  String headId,@Param("planDateWid")  String planDateWid);


    /**
     * 同步推送状态
     */
    void synchPushStatus();

    /**
     * 更新plan表的完成状态
     */
    void updatePlanToFinish();

    /**
     * 更新头表为完成状态(预售)
     */
    void updatePreheatHeaderToDone();

    /**
     * 更新头表为完成状态(正式)
     */
    void updateFormalHeaderToDone();
}
