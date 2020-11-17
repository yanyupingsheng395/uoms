package com.linksteady.qywx.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.domain.SysInfoBo;
import com.linksteady.common.service.CommonFunService;
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

    @Autowired
    private CommonFunService commonFunService;

    /**
     * 获取企业微信应用配置
     * @return
     */
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

    /**
     * 修改企业微信应用配置
     * @param corpId  企业微信ID
     * @param secret  应用秘钥
     * @return
     */
    @PostMapping("/updateCorpInfo")
    public ResponseBo updateCorpInfo(String corpId,String secret){
        try {
            qywxService.updateCorpInfo(corpId, secret);
            return ResponseBo.ok();
        } catch (Exception e) {
            return ResponseBo.error();
        }
    }

    /**
     * 获取外部联系人信息
     * @return
     */
    @GetMapping("/getContact")
    public ResponseBo getContact(){
        String eventToken = qywxService.getEcEventToken();
        String eventAesKey = qywxService.getEcEventAesKey();
        SysInfoBo qywx = commonFunService.getSysInfoByCode("qywx");
        String eventUrl="";
        if(qywx!=null){
            String domain = qywx.getSysDomain();
            if(StringUtils.isNotEmpty(domain)){
                eventUrl=domain+"/contractEvent/event";
            }
        }
        Map<String,Object> result= Maps.newHashMap();
        result.put("eventToken",eventToken);
        result.put("eventAesKey",eventAesKey);
        result.put("eventUrl",eventUrl);
        return ResponseBo.ok(result);
    }

    /**
     * 更新外部联系人信息
     * @return
     */
    @PostMapping("/updateContact")
    public ResponseBo updateContact(String eventToken,String eventAesKey){
        try {
            qywxService.updateContact(eventToken, eventAesKey);
            return ResponseBo.ok();
        }catch (Exception e){
            return ResponseBo.error();
        }
    }
}
