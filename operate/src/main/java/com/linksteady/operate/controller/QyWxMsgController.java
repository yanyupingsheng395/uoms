package com.linksteady.operate.controller;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.QyWxMsg;
import com.linksteady.operate.service.QyWxMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hxcao
 * @date 2020/5/15
 */
@RestController
@RequestMapping("/qywx")
public class QyWxMsgController {

    @Autowired
    private QyWxMsgService qyWxMsgService;

    @RequestMapping("/saveData")
    public ResponseBo saveData(QyWxMsg qyWxMsg) {
        qyWxMsgService.saveData(qyWxMsg);
    }
}
