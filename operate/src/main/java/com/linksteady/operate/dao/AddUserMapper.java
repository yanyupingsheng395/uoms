package com.linksteady.operate.dao;

import com.linksteady.common.domain.Ztree;
import com.linksteady.operate.domain.AddUserConfig;
import com.linksteady.operate.domain.AddUserHead;
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

    void saveConfigData(AddUserConfig addUserConfig);

    void deleteHead(String id);

    void deleteConfig(String headId);

    AddUserConfig getConfigByHeadId(String headId);

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
}
