package com.linksteady.operate.dao;

import com.linksteady.operate.domain.DailyGroupTemplate;
import com.linksteady.operate.domain.DailyInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2019-07-31
 */
public interface DailyMapper {

    List<DailyInfo> getPageList(int start, int end, String touchDt);

    List<DailyInfo> getTouchPageList(int start, int end, String touchDt);

    int getTotalCount(@Param("touchDt") String touchDt);

    int getTouchTotalCount(@Param("touchDt") String touchDt);

    Map<String, Object> getTipInfo(@Param("headId") String headId);

    void updateStatus(@Param("headId") String headId, @Param("status") String status);

    void updateActualNum(String headId, int num);

    String getStatusById(String headId);

    DailyInfo getKpiVal(String headId);

    String getTouchDt(String headId);

    Map<String, Object> getTotalNum(String headId);

    DailyInfo findById(String headId);

    int getUserGroupCount();

    List<DailyGroupTemplate> getUserGroupListPage(int start, int end);

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
}
