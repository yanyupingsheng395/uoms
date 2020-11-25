package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.PushListInfo;

import java.util.List;
import java.util.Map;

public interface PushListMapper {

    int getTotalCount(String sourceCode, String pushStatus, String pushDateStr);

    List<PushListInfo> getPushInfoListPage(int limit, int offset, String sourceCode, String pushStatus, String pushDateStr);

    void insertTestMsg(PushListInfo pushListInfo);

    Map<String, Object> getPushDataOfDay(int day);

    Map<String, Object> getPushDataOfMonth(String startDt, String endDt);

    Map<String, Object> getRptAndBlackData(int day, String startDt, String endDt);
}
