package com.linksteady.operate.service;

import com.linksteady.common.domain.Ztree;
import com.linksteady.operate.domain.AddUserConfig;
import com.linksteady.operate.domain.AddUserHead;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/7/16
 */
public interface AddUserService {

    int getHeadCount();

    List<AddUserHead> getHeadPageList(int limit, int offset);

    /**
     * 对主记录进行保存
     * @param addUserHead
     */
    void saveData(AddUserHead addUserHead);

    void deleteTask(String id);

    void editConfig(AddUserConfig addUserConfig);

    List<Map<String, String>> getSource();

    /**
     * 针对选择的条件，筛选用户，并返回筛选后的信息
     */
    void filterUsers(long headId, String sourceId, String regionIds)  throws Exception;

    AddUserHead getHeadById(long id);
    /**
     * 执行一次拉新推送任务
     * @param headId
     */
    void execTask(long headId,String opUserName) throws Exception;

    AddUserHead saveDailyUserData(String headId, String dailyUserCnt, String dailyApplyRate);

    void updateSmsContentAndContactWay(String headId, String smsContent, String contactWayId, String contactWayUrl);

    /**
     * 更新推送状态 （每日调度任务用)
     */
    void autoUpdateStatus();

    /**
     * 计算任务的效果
     */
    void calculateAddUserEffect();

    Map<String, Object> getTaskResultData(String headId);

    List<Map<String, Object>> getStatisApplyData(String headId, String scheduleId);
}
