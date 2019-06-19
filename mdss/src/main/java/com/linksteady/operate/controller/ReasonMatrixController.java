package com.linksteady.operate.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.operate.service.ReasonMatrixService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/reasonMartix")
public class ReasonMatrixController {

    @Autowired
    private ReasonMatrixService reasonMatrixService;

    @GetMapping("/getMartix")
    public ResponseBo getMatrix(@RequestParam("reasonId") String reasonId) {
        try{
            Map<String, Object> res = reasonMatrixService.getMatrix(reasonId);
            return ResponseBo.okWithData(null, res);
        }catch (Exception ex) {
            log.error("ReasonMatrixController:", ex);
            return ResponseBo.error();
        }
    }
}
