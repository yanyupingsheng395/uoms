//package com.linksteady.qywx.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.linksteady.common.domain.ResponseBo;
//import com.linksteady.common.domain.enums.ConfigEnum;
//import com.linksteady.common.util.OkHttpUtil;
//import com.linksteady.common.util.crypto.SHA1;
//import com.linksteady.qywx.exception.WxErrorException;
//import com.linksteady.qywx.service.FollowUserService;
//import com.linksteady.qywx.service.QywxTaskResultService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.assertj.core.util.Lists;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.Date;
//import java.util.List;
//
///**
// * 企微任务 获取结果
// * @author huang
// * @date 2020/9/17
// */
//@Slf4j
//@RestController
//@RequestMapping("/qywxTaskResult")
//public class QywxTaskResultController {
//
//    @Autowired
//    private QywxTaskResultService qywxTaskResultService;
//
//    /**
//     * 同步推送任务的执行结果
//     * @param request
//     * @return
//     */
//    @RequestMapping("/syncPushResult")
//    public ResponseBo syncPushResult(HttpServletRequest request) {
//        try {
//            qywxTaskResultService.syncPushResult();
//            qywxTaskResultService.updateExecStatus();
//            return ResponseBo.ok();
//        } catch (WxErrorException e) {
//            return ResponseBo.error(e.getMessage());
//        }
//
//
//    }
//}
