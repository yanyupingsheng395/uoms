package com.linksteady.operate.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.PushListInfo;
import com.linksteady.operate.push.PushListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-09-28
 */
@RestController
@RequestMapping("/push")
public class PushController {

    @Autowired
    private PushListService pushListService;

    @GetMapping("/getPushInfoListPage")
    public ResponseBo getPushInfoListPage(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String sourceCode = request.getParam().get("sourceCode");
        String pushStatus = request.getParam().get("pushStatus");
        String pushDateStr = request.getParam().get("pushDateStr");
        List<PushListInfo> dataList = pushListService.getPushInfoListPage(start, end, sourceCode, pushStatus, pushDateStr);
        int count = pushListService.getTotalCount(sourceCode, pushStatus, pushDateStr);
        return ResponseBo.okOverPaging(null, count, dataList);
    }
}
