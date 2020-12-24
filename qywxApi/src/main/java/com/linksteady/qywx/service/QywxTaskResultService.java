package com.linksteady.qywx.service;

import com.linksteady.qywx.exception.WxErrorException;

public interface QywxTaskResultService {

    void syncPushResult()  throws WxErrorException;

    void updateDailyExecStatus();

    void updateActivityExecStatus();

    void updateManualExecStatus();

    void manualSyncMsgResult(String msgId) throws WxErrorException;

}
