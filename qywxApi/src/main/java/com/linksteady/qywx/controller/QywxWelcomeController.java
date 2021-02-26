package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.QywxWelcome;
import com.linksteady.qywx.service.WelcomeService;
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
    private WelcomeService qywxWelcomeService;

    @PostMapping("/saveData")
    public ResponseBo saveData(QywxWelcome qywxWelcome) {
        return ResponseBo.okWithData(null, qywxWelcomeService.saveData(qywxWelcome));
    }

    @PostMapping("/updateData")
    public ResponseBo updateData(QywxWelcome qywxWelcome) {
        qywxWelcomeService.updateData(qywxWelcome);
        return ResponseBo.ok();
    }

    @GetMapping("/getDataTableList")
    public ResponseBo getDataTableList(Integer limit, Integer offset) {
        int count = qywxWelcomeService.getDataCount();
        List<QywxWelcome> dataList = qywxWelcomeService.getDataList(limit, offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @PostMapping("/deleteById")
    public ResponseBo deleteById(long id) {
        qywxWelcomeService.deleteById(id);
        return ResponseBo.ok();
    }

    @PostMapping("/updateStatus")
    public ResponseBo updateStatus(long id, String status) {
        qywxWelcomeService.updateStatus(id, status);
        return ResponseBo.ok();
    }
}
