package com.linksteady.wxofficial.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.entity.po.WxPushHead;
import com.linksteady.wxofficial.service.WxMsgPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author hxcao
 * @date 2020/4/29
 */
@RestController
@RequestMapping("/wxMsgPush")
public class WxMsgPushController {

    @Autowired
    private WxMsgPushService wxMsgPushService;

    @Autowired
    private WxProperties wxProperties;

    @RequestMapping("/getDataList")
    public ResponseBo getDataList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        int count = wxMsgPushService.getCount();
        List<WxPushHead> dataList = wxMsgPushService.getDataList(limit, offset);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    @RequestMapping("/saveData")
    public ResponseBo saveData(WxPushHead wxPushHead) {
        wxMsgPushService.saveData(wxPushHead);
        return ResponseBo.ok();
    }

    @RequestMapping("/deleteById")
    public ResponseBo deleteById(String id) {
        wxMsgPushService.deleteById(id);
        return ResponseBo.ok();
    }

    @GetMapping("/getPushInfo")
    public ResponseBo getPushInfo() {
        int hour = LocalTime.now().getHour();
        final List<String> timeList = IntStream.rangeClosed(8, 22).filter(x -> x > hour).boxed().map(y -> {
            if (y < 10) {
                return "0" + y + ":00";
            }
            return y + ":00";
        }).collect(Collectors.toList());
        return ResponseBo.okWithData(null, timeList);
    }

    @RequestMapping("/pushMsg")
    public ResponseBo pushMsg() {
        wxMsgPushService.pushMsg();
        return ResponseBo.ok();
    }
}
