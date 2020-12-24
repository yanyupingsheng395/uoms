package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxTaskResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 企微任务 获取结果
 * @author huang
 * @date 2020/9/17
 */
@Slf4j
@RestController
@RequestMapping("/qywxTaskResult")
public class QywxTaskResultController {

    @Autowired
    private QywxTaskResultService qywxTaskResultService;

    /**
     * 同步推送任务的执行结果
     * @param request
     * @return
     */
    @RequestMapping("/syncPushResult")
    public ResponseBo syncPushResult(HttpServletRequest request) {
        try {
            qywxTaskResultService.syncPushResult();
            qywxTaskResultService.updateDailyExecStatus();
            return ResponseBo.ok();
        } catch (WxErrorException e) {
            return ResponseBo.error(e.getMessage());
        }
    }

    /**
     * 手工同步企业微信执行结果
     */
    @RequestMapping("/manualSyncMsgResult")
    public ResponseBo manualSyncMsgResult(String msgId) {
        try {
            qywxTaskResultService.manualSyncMsgResult(msgId);
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("同步企业微信消息执行结果失败，失败原因为{}，错误原因为{}",e);
            return ResponseBo.error(e.getMessage());
        }
    }
}
