package com.linksteady.operate.controller;

import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.domain.SpuCycle;
import com.linksteady.operate.service.SpuCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Created by hxcao on 2019-04-29
 */
@RestController
@RequestMapping("/spucycle")
public class SpuCycleController {

    @Autowired
    private SpuCycleService spuCycleService;

    @PostMapping("/list")
    public ResponseBo getDataList(@RequestBody QueryRequest request, String spuId) {
        List<SpuCycle> result=spuCycleService.getDataList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize(), spuId);
        int totalCount= spuCycleService.getTotalCount(spuId);
        return  ResponseBo.okOverPaging("",totalCount,result);
    }
}
