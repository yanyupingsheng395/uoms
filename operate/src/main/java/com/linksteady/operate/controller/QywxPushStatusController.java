package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.config.PushConfig;
import com.linksteady.operate.domain.enums.PushSignalEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qywxpush")
@Slf4j
public class QywxPushStatusController extends BaseController {

    @Autowired
    private PushConfig pushConfig;

    /**
     * 关闭推送服务
     * @param
     * @return
     */
    @GetMapping("/stop")
    public ResponseBo stop() {
        //判断当前状态
        if("Y".equalsIgnoreCase(pushConfig.getPushFlag())) {
            //关闭服务
            try {
               // pushConfig.sendPushSignal(PushSignalEnum.SIGNAL_STOP,getCurrentUser().getUsername());
                return ResponseBo.ok("关闭服务成功！");
            } catch (Exception e) {
                log.error("关闭推送服务异常，异常原因为{}",e);
                return ResponseBo.error("关闭服务失败！");
            }
        }else
        {
            return ResponseBo.error("关闭服务失败，服务已经是关闭状态！");
        }
    }

    /**
     * 开启推送服务
     * @param
     * @return
     */
    @GetMapping("/start")
    public ResponseBo start() {
        //判断当前状态
        if("N".equalsIgnoreCase(pushConfig.getQywxPushFlag())) {
            //开启服务
            try {
               // pushConfig.sendPushSignal(PushSignalEnum.SIGNAL_START,getCurrentUser().getUsername());
                return ResponseBo.ok("开启服务成功！");
            } catch (Exception e) {
                log.error("开启推送服务异常，异常原因为{}",e);
                return ResponseBo.error("开启服务失败！");
            }
        }else
        {
            return ResponseBo.error("开启服务失败,服务已经是开启状态！");
        }
    }

    /**
     * 获取微信当前服务的启动状态
     * @param
     * @return
     */
    @GetMapping("/status")
    public ResponseBo status() {
        return ResponseBo.okWithData(null, pushConfig.getQywxPushFlag());
    }

}
