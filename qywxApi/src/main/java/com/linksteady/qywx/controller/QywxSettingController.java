package com.linksteady.qywx.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.storage.impl.RedisConfigStorageImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/qywx")
public class QywxSettingController {

    @Autowired
    private QywxService qywxService;


    @GetMapping("/getQywxParam")
    public ResponseBo getQywxParam(){
        RedisConfigStorageImpl storage = qywxService.getRedisConfigStorage();
        String corpId = storage.getCorpId();
        String secret = storage.getSecret();
        Map<String,Object> result= Maps.newHashMap();
        if(StringUtils.isNotEmpty(corpId)&&StringUtils.isNotEmpty(secret)){
            result.put("corpId",corpId);
            result.put("secret",secret);
            result.put("msg","Y");
        }else {
            result.put("msg","N");
        }
        return ResponseBo.ok(result);
    }

    @PostMapping("/updateCorpInfo")
    public ResponseBo updateCorpInfo(String corpId,String secret){
        try {
            qywxService.updateCorpInfo(corpId, secret);
            return ResponseBo.ok();
        } catch (Exception e) {
            return ResponseBo.error();
        }
    }
}
