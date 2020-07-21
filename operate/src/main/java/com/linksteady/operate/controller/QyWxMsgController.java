package com.linksteady.operate.controller;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.QywxMsg;
import com.linksteady.operate.service.QywxMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/5/15
 */
@RestController
@RequestMapping("/qywx")
public class QyWxMsgController {

    @Autowired
    private QywxMsgService qywxMsgService;

    @RequestMapping("/saveData")
    public ResponseBo saveData(QywxMsg qyWxMsg) {
        qywxMsgService.saveData(qyWxMsg);
        return ResponseBo.ok();
    }

    @RequestMapping("/getDataListPage")
    public ResponseBo getDataListPage(Integer limit, Integer offset) {
        List<QywxMsg> dataList = qywxMsgService.getDataListPage(limit, offset);
        int count = qywxMsgService.getTotalCount();
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @RequestMapping("/updateData")
    public ResponseBo updateData(QywxMsg qyWxMsg) {
        qywxMsgService.updateQyWxMsg(qyWxMsg);
        return ResponseBo.ok();
    }

    @RequestMapping("/deleteDataById")
    public ResponseBo deleteDataById(@RequestParam("id") String id) {
        qywxMsgService.deleteDataById(id);
        return ResponseBo.ok();
    }

    @RequestMapping("/getDataById")
    public ResponseBo getDataById(@RequestParam("id") String id) {
        return ResponseBo.okWithData(null, qywxMsgService.getDataById(id));
    }
}
