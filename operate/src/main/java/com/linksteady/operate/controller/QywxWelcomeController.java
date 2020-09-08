package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.QywxWelcome;
import com.linksteady.operate.service.QywxWelcomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/9/3
 */
@RestController
@RequestMapping("/welcome")
public class QywxWelcomeController {

    @Autowired
    private QywxWelcomeService qywxWelcomeService;

    @PostMapping("/saveData")
    public ResponseBo saveData(QywxWelcome qywxWelcome) {
        qywxWelcomeService.saveData(qywxWelcome);
        return ResponseBo.ok();
    }

    @GetMapping("/getDataTableList")
    public ResponseBo getDataTableList(Integer limit, Integer offset) {
        int count = qywxWelcomeService.getDataCount();
        List<QywxWelcome> dataList = qywxWelcomeService.getDataList(limit, offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }
}
