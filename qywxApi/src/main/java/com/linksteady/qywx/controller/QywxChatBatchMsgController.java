package com.linksteady.qywx.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.QywxChatBatchMsg;
import com.linksteady.qywx.service.QywxChatBatchMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/qywxChatMsg")
@Slf4j
public class QywxChatBatchMsgController  extends BaseController {

    @Autowired
    private QywxChatBatchMsgService qywxChatBatchMsgService;

    /**
     * 获取客户群列表
     *
     */
    @RequestMapping("/getMeList")
    @ResponseBody
    public ResponseBo getMeList(QueryRequest request){
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count = qywxChatBatchMsgService.getCount();
        List<QywxChatBatchMsg> lists= qywxChatBatchMsgService.getDataList(limit, offset);
        return ResponseBo.okOverPaging(null,count,lists);
    }

    /**
     * 新增群发消息
     * @param qywxChatBatchMsg
     * @return
     */
    @PostMapping("/saveData")
    public ResponseBo saveData(QywxChatBatchMsg qywxChatBatchMsg) {
        String[] chatArray = qywxChatBatchMsg.getChatOwnerList().split(",");
        qywxChatBatchMsg.setChatOwnerSize(chatArray.length);
        qywxChatBatchMsg.setInsertBy(getCurrentUser().getUsername());
        qywxChatBatchMsg.setInsertDt(new Date());
        qywxChatBatchMsgService.saveData(qywxChatBatchMsg);
        return ResponseBo.ok();
    }

    @RequestMapping("/deleteById")
    public ResponseBo deleteById(long id){
        qywxChatBatchMsgService.deleteById(id);
        return ResponseBo.ok();
    }

    /**
     * 群发消息推送
     * @param batchMsgId
     * @return
     */
    @RequestMapping("/pushMessage")
    public ResponseBo pushMessage(@RequestParam("batchMsgId")long batchMsgId){
        try {
            qywxChatBatchMsgService.pushMessage(batchMsgId);
            return ResponseBo.ok("群发消息成功，请查看推送状态！");
        } catch (Exception e) {
           log.info("执行群发消息失败{}",e);
           return ResponseBo.error("群发消息失败！");
        }
    }

}
