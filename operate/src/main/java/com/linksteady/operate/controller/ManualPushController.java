package com.linksteady.operate.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.ManualHeader;
import com.linksteady.operate.service.ManualPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 短信手动推送 controller
 *
 * @author hxcao
 * @date 2019/12/25
 */
@RestController
@RequestMapping("/manual")
public class ManualPushController {

    @Autowired
    private ManualPushService manualPushService;

    /**
     * 获取分页数据
     *
     * @param request
     * @return
     */
    @GetMapping("/getHeaderListPage")
    public ResponseBo getHeaderListPage(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        int count = manualPushService.getHeaderListCount();
        List<ManualHeader> dataList = manualPushService.getHeaderListData(start, end);
        return ResponseBo.okOverPaging(null, count, dataList);
    }

    /**
     * 保存头表和行表数据
     * @return
     */
    @PostMapping("/saveManualData")
    public synchronized ResponseBo saveManualData(@RequestParam("smsContent") String smsContent, @RequestParam("file") MultipartFile file, @RequestParam("sendType")  String sendType, String pushDate) throws IOException {
        manualPushService.saveManualData(smsContent, file, sendType, pushDate);
        return ResponseBo.ok();
    }

    /**
     * 同步推送短信内容，防重复推送。锁+状态
     * @return
     */
    @PostMapping("/pushMessage")
    public synchronized ResponseBo pushMessage(@RequestParam("headId") String headId, @RequestParam("pushType") String pushType) {
        String status = manualPushService.getHeadStatus(headId);
        // 已上传，待推送
        if(status.equalsIgnoreCase("0")) {
            manualPushService.pushMessage(headId, pushType);
        }else {
            return ResponseBo.error("该记录当前状态不支持推送操作！");
        }
        return ResponseBo.ok();
    }

    @GetMapping("/getPushInfo")
    public ResponseBo getPushInfo(@RequestParam("headId") String headId) {
        return ResponseBo.okWithData(null, manualPushService.getPushInfo(headId));
    }
}
