package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.service.QywxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/welcome")
public class WelcomeController {

    @Autowired
    QywxService qywxService;


    /**
     * 打开欢迎语
     */
    @RequestMapping("/openWelcome")
    public ResponseBo openWelcome() {
        qywxService.setEnableWelcome("Y");
        return ResponseBo.ok();
    }

    /**
     * 关闭欢迎语
     */
    @RequestMapping("/closeWelcome")
    public ResponseBo closeWelcome() {
        qywxService.setEnableWelcome("N");
        return ResponseBo.ok();
    }
}
