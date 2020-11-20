package com.linksteady.qywx.dao;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/8/18
 */
public interface AddUserMonitorMapper {

    List<Map<String, Object>> getApplySuccessData(String startDt, String endDt, String dateFormat);

    /**
     * 获取推送人数，申请通过人数，转化率
     * @return
     */
    List<Map<String, Object>> getPassivePushAndApplyData(String startDt, String endDt, String dateFormat);

    /**
     * 获取推送人数，申请通过人数，转化率
     * @return
     */
    List<Map<String, Object>> getTriggerPushAndApplyData(String startDt, String endDt, String dateFormat);
}
