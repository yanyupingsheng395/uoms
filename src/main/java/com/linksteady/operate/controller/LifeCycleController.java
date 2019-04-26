package com.linksteady.operate.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
     * 获取spu列表
     * @param
     * @return
     */
    @RequestMapping("/getSpuList")
    public ResponseBo lifecycleCatList(@RequestParam   String startDt,@RequestParam  String endDt,String filterType,String source) {

        return  ResponseBo.ok("");
    }

    /**
     * 获取SPU的筛选条件列表
     */
    @RequestMapping("/getSpuFilterList")
    public ResponseBo getSpuFilterList() {

        List<Map<String,String>> result= Lists.newArrayList();

        Map<String,String> temp= Maps.newHashMap();
        temp.put("gmv","GMV贡献率");
        result.add(temp);

        temp= Maps.newHashMap();
        temp.put("user","用户数");
        result.add(temp);

        temp= Maps.newHashMap();
        temp.put("pocount","订单数");
        result.add(temp);

        temp= Maps.newHashMap();
        temp.put("joinrate","连带率");
        result.add(temp);

        temp= Maps.newHashMap();
        temp.put("sprice","件单价");
        result.add(temp);

        temp= Maps.newHashMap();
        temp.put("profit","毛利率");
        result.add(temp);

        return  ResponseBo.okWithData("",result);
    }


}
