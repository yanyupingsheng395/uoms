package com.linksteady.qywx.controller;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxGropMsgService;
import com.linksteady.qywx.service.QywxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    @Autowired
    QywxGropMsgService qywxGropMsgService;

    @Autowired
    QywxService qywxService;

    /**
     * 返回接口应用的状态
     */
    @RequestMapping("/status")
    public ResponseBo status() {
        return ResponseBo.ok("up");
    }

    @PostMapping("/addMsgTemplate")
    public String addMsgTemplate(String addparam) {
        try {
            return qywxGropMsgService.addMsgTemplate( JSONObject.parseObject(addparam));
        } catch (WxErrorException e) {
            return "";
        }
    }

    /**
     * 获取corpID
     */
    @RequestMapping("/getCorpId")
    public String getCorpId() {
        try {
            return qywxService.getRedisConfigStorage().getCorpId();
        } catch (Exception e) {
           log.error("获取企业微信所属公司出错，错误原因为{}",e);
            return "";
        }
    }

    /**
     * 获取mpAppId
     */
    @RequestMapping("/getMpAppId")
    public String getMpAppId() {
        try {
            return qywxService.getRedisConfigStorage().getMpAppId();
        } catch (Exception e) {
            log.error("获取企业微信关联的小程序出错，错误原因为{}",e);
            return "";
        }
    }

}
