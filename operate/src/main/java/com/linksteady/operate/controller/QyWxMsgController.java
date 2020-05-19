package com.linksteady.operate.controller;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.QyWxMsg;
import com.linksteady.operate.service.QyWxMsgService;
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
    private QyWxMsgService qyWxMsgService;

    @RequestMapping("/saveData")
    public ResponseBo saveData(QyWxMsg qyWxMsg) {
        qyWxMsgService.saveData(qyWxMsg);
        return ResponseBo.ok();
    }

    @RequestMapping("/getDataListPage")
    public ResponseBo getDataListPage(Integer limit, Integer offset) {
        List<QyWxMsg> dataList = qyWxMsgService.getDataListPage(limit, offset);
        int count = qyWxMsgService.getTotalCount();
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @RequestMapping("/updateData")
    public ResponseBo updateData(QyWxMsg qyWxMsg) {
        qyWxMsgService.updateQyWxMsg(qyWxMsg);
        return ResponseBo.ok();
    }

    @RequestMapping("/deleteDataById")
    public ResponseBo deleteDataById(@RequestParam("id") String id) {
        qyWxMsgService.deleteDataById(id);
        return ResponseBo.ok();
    }

    @RequestMapping("/getDataById")
    public ResponseBo getDataById(@RequestParam("id") String id) {
        return ResponseBo.okWithData(null, qyWxMsgService.getDataById(id));
    }
}
