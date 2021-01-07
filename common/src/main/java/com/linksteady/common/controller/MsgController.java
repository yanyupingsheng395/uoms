package com.linksteady.common.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.MsgInfo;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/3/16
 */
@RestController
@RequestMapping("/msg")
public class MsgController extends BaseController {

    @Autowired
    private MsgService msgService;

    /**
     * 获取最近的10条消息列表
     * @return
     */
    @RequestMapping("/getMsgList")
    public ResponseBo getMsgList() {
        Map<String, Object> result = Maps.newHashMap();
        int dataCount = msgService.getDataCount("", "0");
        final List<MsgInfo> msgList = msgService.getMsgList();
        result.put("dataCount", dataCount);

        if(null!=msgList&&msgList.size()>10)
        {
            result.put("msgList", msgList.subList(0,10));
        }else
        {
            result.put("msgList", msgList);
        }

        return ResponseBo.okWithData(null, result);
    }

    /**
     * 分页获取消息列表
     * @param request
     * @return
     */
    @RequestMapping("/getMsgPageList")
    public ResponseBo getMsgPageList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String msgLevel = request.getParam().get("msgLevel");
        String readFlag = request.getParam().get("readFlag");
        List<MsgInfo> msgInfos = msgService.getMsgPageList(msgLevel, readFlag, limit, offset);
        int count = msgService.getDataCount(msgLevel, readFlag);
        return ResponseBo.okOverPaging(null, count, msgInfos);
    }

    /**
     * 更改消息为已读
     * @param msgId
     * @return
     */
    @GetMapping("/updateMsgRead")
    public ResponseBo updateMsgRead(Long msgId) {
        if(msgId==null)
        {
            msgService.updateAllMsgRead();
        }else
        {
            msgService.updateMsgRead(msgId);
        }
        return ResponseBo.ok();
    }

    /**
     * 更新所有消息为已读
     * @return
     */
    @GetMapping("/updateMsgAllRead")
    public ResponseBo updateMsgAllRead() {
        msgService.updateAllMsgRead();
        return ResponseBo.ok();
    }
}
