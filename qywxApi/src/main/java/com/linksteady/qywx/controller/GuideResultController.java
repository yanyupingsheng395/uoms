package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.service.GuideResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 导购运营结果相关的controller
 * @author hxcao
 * @date 2020/9/17
 */
@RestController
@RequestMapping("/guideResult")
public class GuideResultController extends VerifyController {

    @Autowired
    private GuideResultService guideResultService;

    /**
     * 获取导购运行结果的数据
     * @param request
     * @param signature
     * @param timestamp
     * @param userId
     * @param startDt
     * @param endDt
     * @return
     */
    @RequestMapping("/getResultData")
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
        return ResponseBo.okWithData(null, guideResultService.getResultData(userId, startDt, endDt));
    }
}
