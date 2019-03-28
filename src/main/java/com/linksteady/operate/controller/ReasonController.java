package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.service.ReasonService;
import com.linksteady.operate.vo.KeyPointMonthVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 原因探究相关的controller
 */
@RestController
@RequestMapping("/reason")
public class ReasonController  extends BaseController {

    @Autowired
    ReasonService reasonService;

    /**
     * 获取原因探究列表
     * @param request  包装分页参数的对象
     * @return
     */
    @RequestMapping("/list")
    public ResponseBo list(QueryRequest request) {
        List<Map<String,Object>> result=reasonService.getReasonList(request.getPageNum()*request.getPageSize()+1, (request.getPageNum()+1)*request.getPageSize());

        int totalCount= reasonService.getReasonTotalCount();
        return  ResponseBo.okOverPaging("",totalCount,result);
    }

}
