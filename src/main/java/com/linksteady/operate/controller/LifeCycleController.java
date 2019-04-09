package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.service.LifeCycleService;
import com.linksteady.operate.service.ReasonService;
import com.linksteady.operate.vo.ReasonVO;
import com.linksteady.system.domain.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 原因探究相关的controller
 */
@RestController
@RequestMapping("/lifecycle")
public class LifeCycleController extends BaseController {

    @Autowired
    LifeCycleService lifeCycleService;


    /**
     * 获取品类列表
     * @param
     * @return
     */
    @RequestMapping("/getCatList")
    public ResponseBo lifecycleCatList(@RequestBody  QueryRequest request,@RequestParam   String cateName,@RequestParam  String orderColumn) {
        List<Map<String,Object>> result=lifeCycleService.getCatList((request.getPageNum()-1)*request.getPageSize()+1, request.getPageNum()*request.getPageSize(),orderColumn,cateName);

        int totalCount= lifeCycleService.getCatTotalCount(cateName);
        return  ResponseBo.okOverPaging("",totalCount,result);
    }

}
