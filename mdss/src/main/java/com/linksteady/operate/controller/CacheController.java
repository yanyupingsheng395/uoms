package com.linksteady.operate.controller;

import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 缓存相关的controller
 * @author huang
 */
@RestController
@RequestMapping("/cache")
public class CacheController extends BaseController {

    @Autowired
    CacheService cacheService;

    /**
     * 重新加载缓存
     * @param
     * @return
     */
    @RequestMapping("/refreshCache")
    public ResponseBo refreshCache() {
        cacheService.procAllLoad();
        return ResponseBo.ok("success");
    }
}

