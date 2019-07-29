package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.service.FitDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 品类生命周期拟合曲线
 * @author hxcao
 * @date 2019-07-26
 */
@RestController
@RequestMapping("/fitdata")
public class FitDataController {

    @Autowired
    private FitDataService fitDataService;

    @GetMapping("/generateFittingData")
    public ResponseBo generateFittingData(String spuId, String purchCount, @RequestParam  String type) {
        if(!"".equals(purchCount)) {
            List<Integer> purchTimes = Arrays.asList(purchCount.split(",")).stream().map(x->Integer.valueOf(x)).collect(Collectors.toList());
            return ResponseBo.okWithData(null, fitDataService.generateFittingData(spuId, purchTimes, type));
        }
        return null;
    }
}
