package com.linksteady.wxofficial.controller;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.wxofficial.common.wechat.service.OperateService;
import com.linksteady.wxofficial.config.WxProperties;
import com.linksteady.wxofficial.entity.po.WxPushHead;
import com.linksteady.wxofficial.service.WxMsgPushService;
import me.chanjar.weixin.mp.bean.WxMpMassOpenIdsMessage;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
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

    @Autowired
    private OperateService operateService;

    @RequestMapping("/pushMsg")
    public ResponseBo pushMsg(String pushMethod, String pushPeriod, String headId) {
        wxMsgPushService.pushMsg(pushMethod, pushPeriod, headId);
        return ResponseBo.ok();
    }

    @RequestMapping("/getMsgHeadById")
    public ResponseBo getMsgHeadById(String headId) {
        return ResponseBo.okWithData(null, wxMsgPushService.getMsgHeadById(headId));
    }

    @RequestMapping("/test")
    public ResponseBo test() {
        String url = wxProperties.getServiceDomain() + wxProperties.getBatchPushUrl();
        String getStatusUrl = wxProperties.getServiceDomain() + wxProperties.getMessageMassGetUrl();
        Map<String, Object> param = Maps.newHashMap();
        List<String> toUsers = Lists.newArrayList();
        toUsers.add("okyDRv_5AWhd3dC-MSiTyDc9WtRY");
        toUsers.add("okyDRv_OACg8EPYDsInNkiIj6pHw");
        WxMpMassOpenIdsMessage message = new WxMpMassOpenIdsMessage();
        message.setToUsers(toUsers);
        message.setContent("test");
        message.setMediaId("");
        message.setMsgType("image");
        param.put("message", message);
        System.out.println(message);
        String result = operateService.callPostBody(url, param);

        WxMpMassSendResult wxMpMassSendResult = JSON.parseObject(result).toJavaObject(WxMpMassSendResult.class);
        String msgId = wxMpMassSendResult.getMsgId();
        Map<String, String> param2 = Maps.newHashMap();
        param2.put("msgId", msgId);
        String statusResult = operateService.callPostForm(getStatusUrl, param2);
        System.out.println(statusResult);
        return ResponseBo.ok();
    }
}
