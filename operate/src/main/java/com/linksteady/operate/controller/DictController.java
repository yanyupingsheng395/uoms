package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author hxcao
 * @date 2020/3/7
 */
@RestController
@RequestMapping("/dict")
public class DictController {

    @Autowired
    private DictService dictService;

    @GetMapping("/getDataListByTypeCode")
    public ResponseBo getDataListByTypeCode(@RequestParam("typeCode") String typeCode) {
        return ResponseBo.okWithData(null, dictService.getDataListByTypeCode(typeCode));
    }
}
