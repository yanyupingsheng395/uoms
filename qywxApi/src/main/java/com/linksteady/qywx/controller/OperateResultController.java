package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.service.OperateResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hxcao
 * @date 2020/9/17
 */
@RestController
@RequestMapping("/operateResult")
public class OperateResultController extends ApiBaseController {

    @Autowired
    private OperateResultService operateResultService;

    @GetMapping("/getResultData")
    public ResponseBo getResultData(HttpServletRequest request,
                                    @RequestParam("signature") String signature,
                                    @RequestParam("timestamp") String timestamp,
                                    @RequestParam("userId") String userId,
                                    @RequestParam("startDt") String startDt, @RequestParam("endDt") String endDt) {
        try {
            validateLegality(request, signature, timestamp, userId);
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
        return ResponseBo.okWithData(null, operateResultService.getResultData(startDt, endDt));
    }
}
