package com.linksteady.operate.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.BlackInfo;
import com.linksteady.operate.service.BlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2020/3/31
 */
@RestController
@RequestMapping("/black")
public class BlackController {

    @Autowired
    private BlackService blackService;

    @GetMapping("/getDataList")
    public ResponseBo getDataList(QueryRequest request) {
        int start = request.getStart();
        int end = request.getEnd();
        String phone = request.getParam().get("phone");
        List<BlackInfo> blackInfos = blackService.getDataList(phone, start, end);
        int count = blackService.getCount(phone);
        return ResponseBo.okOverPaging(null, count, blackInfos);
    }

    @GetMapping("/deleteByPhone")
    public void deleteByPhone(String phone) {
        blackService.deleteByPhone(phone);
    }

    @GetMapping("/insertData")
    public void insertData(BlackInfo blackInfo) {
        blackService.insertData(blackInfo);
    }
}
