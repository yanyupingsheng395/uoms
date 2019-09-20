package com.linksteady.operate.service;

import com.linksteady.operate.domain.DailyGroupTemplate;
import com.linksteady.operate.domain.DailyInfo;
import com.linksteady.operate.sms.domain.TaskInfo;
import com.linksteady.operate.vo.Echart;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyService {

    List<DailyInfo> getPageList(int start, int end, String touchDt);

    List<DailyInfo> getTouchPageList(int start, int end, String touchDt);

    int getTotalCount(String touchDt);

    int getTouchTotalCount(String touchDt);

    Map<String, Object> getTipInfo(String headId);

    void updateStatus(String headId, String status);

    String getStatusById(String headId);

    DailyInfo getKpiVal(String headId);

    Map<String, Object> getCurrentAndTaskDate(String headId);

    TaskInfo getTaskInfo(String headId);

    List<DailyGroupTemplate> getUserGroupListPage(int start, int end);

    int getUserGroupCount();

    void setSmsCode(String groupId, String smsCode);
}
