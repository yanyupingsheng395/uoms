package com.linksteady.qywx.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.domain.QywxChatBatchMsg;
import com.linksteady.qywx.service.QywxChatBatchMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/qywxChatMsg")
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

}
