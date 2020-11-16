package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/wxMedia")
public class MediaController extends VerifyController{

    @Autowired
    MediaService mediaService;

    @RequestMapping("/getMpMediaId")
    public ResponseBo getMpMediaId(HttpServletRequest request,
                                   @RequestParam("signature")String signature,
                                   @RequestParam("timestamp")String timestamp,
                                   @RequestParam("identityType")String identityType,
                                   @RequestParam("identityId") Long identityId){
        try {
            //备注 此接口不进行调用IP校验，因为本地应用也会调用
            return ResponseBo.okWithData(null,mediaService.getMpMediaId(identityType, identityId));
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
    };
}
