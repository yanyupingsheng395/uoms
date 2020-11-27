package com.linksteady.qywx.controller;

import com.linksteady.qywx.domain.QywxUser;
import com.linksteady.qywx.service.GuideResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;


@RestController
@RequestMapping("/operateResult")
@Slf4j
public class OperateResultController {

    @Autowired
    private GuideResultService guideResultService;

    @GetMapping("/getResultData")
    public Map<String, Object> getResultData( String during, String startDt, String endDt) throws URISyntaxException {
//            QywxUser user = (QywxUser) request.getSession().getAttribute("user");
//            Map<String, Object> resultData = guideResultService.getResultData(user.getUserId(), startDt, endDt);
//            log.info("获取到的结果：" + resultData);
//            return resultData;
        return null;
    }
}
