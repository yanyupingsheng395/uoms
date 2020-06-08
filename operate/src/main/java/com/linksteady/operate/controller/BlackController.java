package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.BlackInfo;
import com.linksteady.operate.service.BlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2020/3/31
 */
@RestController
@RequestMapping("/black")
public class BlackController extends BaseController {

    @Autowired
    private BlackService blackService;

    @GetMapping("/getDataList")
    public ResponseBo getDataList(QueryRequest request) {
        int limit = request.getLimit();
        int offset = request.getOffset();
        String phone = request.getParam().get("phone");
        List<BlackInfo> blackInfos = blackService.getDataList(phone, limit,offset);
        int count = blackService.getCount(phone);
        return ResponseBo.okOverPaging(null, count, blackInfos);
    }

    @PostMapping("/deleteData")
    public ResponseBo deleteData(String phone) {
        blackService.deleteByPhone(phone);
        return ResponseBo.ok();
    }

    @PostMapping("/insertData")
    public ResponseBo insertData(BlackInfo blackInfo) {
        blackInfo.setInsertBy(getCurrentUser().getUsername());
        //2表示手工新增
        blackInfo.setInsertType("2");
        blackService.insertData(blackInfo);
        return ResponseBo.ok();
    }

    @GetMapping("/checkPhone")
    public boolean checkPhone(@RequestParam("userPhone") String userPhone) {
        return blackService.checkPhone(userPhone);
    }

    @GetMapping("/test")
    public ResponseBo test() {
        System.out.println(1/0);
        return ResponseBo.ok();
    }
}
