package com.linksteady.operate.dao;

import com.linksteady.operate.domain.ActivityDetail;
import com.linksteady.operate.domain.QywxActivityDetail;
import com.linksteady.operate.domain.QywxPushList;
import com.linksteady.operate.vo.ActivityContentVO;
import com.linksteady.operate.vo.QywxActivityContentTmp;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-08-13
 */
public interface QywxActivityPushMapper {

    /**
     * 删除文案临时表
     */
    void deleteContentTmp(@Param("planId") Long planId);

    /**
     * 获取此活动上配置的所有模板
     */
    List<Map<String,String>> getAllTemplate(Long headId, String activityType);

    List<QywxActivityDetail> getPushList(int limit, int offset, Long planId);

    int getPushCount(Long planId);

    /**
     * 将文案内容写入临时表
     * @param targetList
     */
    void insertPushContentTemp(@Param("list") List<QywxActivityContentTmp> targetList);

    /**
     * 将临时表的文案更新到正式表
     */
    void updatePushContentFromTemp(@Param("planId") Long planId);


    /**
     * 更新状态
     * @param planId
     * @param status
     * @param version
     * @return  返回受影响的记录的条数
     */
    int updateStatus(Long planId, String status, int version);


    /**
     * 将活动的推送数据写入到推送通道表中
     * @param planId
     */
    void insertToPushListLarge(Long planId);

    /**
     * 判断活动是否配置了文案
     */
    int validateSmsConfig(long headId, String planType);

    /**
     * 推送消息(按消息分组)
     * @param headId
     * @param followUserId
     * @return
     */
    List<String> getMessageSignList(Long headId, String followUserId);

    /**
     * 查询当前签名、当前活动的总记录条数
     * @param headId
     * @param followUserId
     * @param qywxMsgSign
     * @return
     */
    int getWaitQywxUserListCount(Long headId, String followUserId, String qywxMsgSign);

    /**
     * 查询当前签名、当前活动的总记录条数
     */
    List<QywxActivityDetail> getQywxUserList(Long headId, String followUserId, String qywxMsgSign, int limit, int offset);

    /**
     *插入运营推送列表
     */
    void insertPushList(QywxPushList qywxPushList);

    /**
     * 没有数据，写入推送列表
     */
    void updatePushList(@Param("pushId") Long pushId, String status, String msgId, String faildList, String remark);

    /**
     * 回写uo_qywx_activity_detail的push_id
     */
    void updatePushId(long minQywxDetailId, long maxQywxDetailId, long pushId,String msgId,String status);
}
