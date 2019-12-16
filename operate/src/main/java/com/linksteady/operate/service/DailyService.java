package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyGroupTemplate;
import com.linksteady.operate.domain.DailyHead;
import com.linksteady.operate.domain.DailyPersonal;
import com.linksteady.operate.vo.DailyPersonalVo;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyService {

    List<DailyHead> getPageList(int start, int end, String touchDt);

    int getTotalCount(String touchDt);


    Map<String, Object> getTipInfo(String headId);

    void updateStatus(String headId, String status);

    String getStatusById(String headId);

    /**
     * 获取每日成长任务的效果统计
     * @param id
     * @return
     */
    DailyHead getEffectById(String id);

    Map<String, Object> getCurrentAndTaskDate(String headId);

    /**
     * 获取推送数据
     * @param headId
     * @return
     */
    Map<String, Object> getPushData(String headId);

    List<DailyGroupTemplate> getUserGroupListPage(int start, int end);

    int getUserGroupCount();

    void setSmsCode(String groupId, String smsCode);

    List<DailyPersonal> getDailyPersonalEffect(DailyPersonalVo dailyPersonalVo, int start, int end, String headId);

    int getDailyPersonalEffectCount(DailyPersonalVo dailyPersonalVo, String headId);

    boolean validUserGroup();
}
