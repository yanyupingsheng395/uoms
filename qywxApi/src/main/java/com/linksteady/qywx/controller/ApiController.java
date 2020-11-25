package com.linksteady.qywx.controller;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.MediaService;
import com.linksteady.qywx.service.QywxGropMsgService;
import com.linksteady.qywx.service.QywxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    @Autowired
    QywxGropMsgService qywxGropMsgService;

    @Autowired
    QywxService qywxService;

    @Autowired
    MediaService mediaService;

    /**
     * 返回接口应用的状态
     */
    @RequestMapping("/status")
    public ResponseBo status() {
        return ResponseBo.ok("up");
    }

    @RequestMapping("/addMsgTemplate")
    public String addMsgTemplate(@RequestBody String data) {
        try {
            log.info("addMsgTemplate--->"+data);
            return qywxGropMsgService.addMsgTemplate( JSONObject.parseObject(data));
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

    @RequestMapping("/getMpMediaId")
    public ResponseBo getMpMediaId(HttpServletRequest request,
                                   @RequestParam("signature")String signature,
                                   @RequestParam("timestamp")String timestamp,
                                   @RequestParam("identityType")String identityType,
                                   @RequestParam("identityId") Long identityId){
        try {
            return ResponseBo.okWithData(null,mediaService.getMpMediaId(identityType, identityId));
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
    }

    /**
     * 获取accessToken
     */
    @RequestMapping("/getAccessToken")
    public String getAccessToken() {
        try {
            return qywxService.getAccessToken();
        } catch (Exception e) {
            log.error("获取企业微信的AccessToken，错误原因为{}",e);
            return "";
        }
    }

}
