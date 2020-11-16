package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.qywx.service.ExternalContactService;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.utils.EventCryptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 外部联系人事件
 * @author huang
 * @date 2020/9/17
 */
@Slf4j
@RestController
@RequestMapping("/contractEvent")
public class ExternalContactEventController {

    @Autowired
    QywxService qywxService;

    /**
     * 接收事件 (验证)
     * @return
     */
    @GetMapping("/event")
    public String event(@RequestParam("echostr") String echostr,
                                   @RequestParam("timestamp") String timestamp,
                                   @RequestParam("nonce") String nonce,
                                   @RequestParam("msg_signature") String msgSignature) {

        log.info(
                "\n接收企业微信系统事件：[msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], echostr=\n{}\n ", msgSignature, timestamp, nonce, echostr);

        // 验证安全签名
        String signature = SHA1.gen(qywxService.getEcEventToken(), timestamp, nonce, echostr);
        if (!signature.equals(msgSignature)) {
            throw new RuntimeException("加密消息签名校验失败");
        }
        String content=EventCryptUtils.decrypt(qywxService.getEcEventAesKey(),echostr);
        log.info("\n消息解密后内容为：\n{} ", content);
        return content;
    }
}
