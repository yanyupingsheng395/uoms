package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.MsgInfo;
import com.linksteady.operate.service.MsgService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hxcao
 * @date 2020/3/16
 */
@RestController
@RequestMapping("/msg")
public class MsgController extends BaseController {

    @Autowired
    private MsgService msgService;

    @RequestMapping("/getMsgList")
    public ResponseBo getMsgList() {
        return ResponseBo.okWithData(null, msgService.getMsgList());
    }

    @RequestMapping("/getMsgPageList")
    public ResponseBo getMsgPageList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String typeCode = request.getParam().get("typeCode");
        String readFlag = request.getParam().get("readFlag");
        List<MsgInfo> msgInfos = msgService.getMsgPageList(typeCode, readFlag, start, end);
        int count = msgService.getDataCount(typeCode, readFlag);
        return ResponseBo.okOverPaging(null, count, msgInfos);
    }

    @GetMapping("/updateMsgRead")
    public ResponseBo updateMsgRead(String msgId) {
        if(StringUtils.isEmpty(msgId))
        {
            msgService.updateAllMsgRead();
        }else
        {
            msgService.updateMsgRead(msgId);
        }
        return ResponseBo.ok();
    }

    @GetMapping("/updateMsgAllRead")
    public ResponseBo updateMsgAllRead() {
        msgService.updateMsgRead(getCurrentUser().getUsername());
        return ResponseBo.ok();
    }

    @GetMapping("/getNoReadCount")
    public ResponseBo getNoReadCount() {
        return ResponseBo.okWithData(null, msgService.getDataCount("", "0"));
    }
}
