package com.linksteady.operate.service;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/8/18
 */
public interface AddUserMonitorService {

    /**
     * 获取企业微信整体拉新效果
     * @param startDt
     * @param endDt
     * @param dateType
     * @return
     */
    List<Map<String, Object>> getApplySuccessData(String startDt, String endDt, String dateType);

    Map<String, Object> getConvertCntAndRate(String startDt, String endDt, String dateType);
}
