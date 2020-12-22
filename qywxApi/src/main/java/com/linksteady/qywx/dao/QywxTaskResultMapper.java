package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxMsgResult;

import java.util.List;

public interface QywxTaskResultMapper {

    List<String> getPushMsgIdList();

    void deletePushResult(String msgId);

    void saveMsgResult(List<QywxMsgResult> qywxMsgResultList);

    void updateDailyExecStatus();

    void updateActivityExecStatus();

    void updateManualExecStatus();

}
