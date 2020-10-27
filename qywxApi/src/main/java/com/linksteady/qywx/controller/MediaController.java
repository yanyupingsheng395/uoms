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
public class MediaController extends ApiBaseController{

    @Autowired
    MediaService mediaService;

    @RequestMapping("/getMpMediaId")
    public ResponseBo getMpMediaId(HttpServletRequest request,
                                   @RequestParam("signature")String signature,
                                   @RequestParam("timestamp")String timestamp,
                                   @RequestParam("identityType")String identityType,
                                   @RequestParam("identityId") Long identityId){
        try {
            validateLegality(request,signature,timestamp,identityType,String.valueOf(identityId));
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
        return ResponseBo.okWithData(null,mediaService.getMpMediaId(identityType, identityId));
    };
}
