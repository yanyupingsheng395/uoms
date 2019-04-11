package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.service.LifeCycleService;
import com.linksteady.operate.service.OpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户运营相关的controller
 */
@RestController
@RequestMapping("/op")
public class OpController extends BaseController {

    @Autowired
    OpService opService;


    /**
     * 获取日运营的头表列表
     * @param
     * @return
     */
    @RequestMapping("/getOpDayList")
    public ResponseBo getOpDayList(@RequestBody  QueryRequest request) {
        List<Map<String,Object>> result=opService.getOpDayList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize(),"");

        int totalCount= opService.getOpDayListCount("");
        return  ResponseBo.okOverPaging("",totalCount,result);
    }

}
