package com.linksteady.operate.controller;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.QueryRequest;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.RandomUtil;
import com.linksteady.operate.config.KpiCacheManager;
import com.linksteady.operate.domain.ReasonRelMatrix;
import com.linksteady.operate.domain.ReasonResult;
import com.linksteady.operate.domain.ReasonTemplateInfo;
import com.linksteady.operate.service.CacheService;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

