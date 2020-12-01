package com.linksteady.qywx.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.constant.CommonConstant;
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
        String agentId=qywxService.getAgentId();
        Map<String,Object> result= Maps.newHashMap();
        if(StringUtils.isNotEmpty(corpId)&&StringUtils.isNotEmpty(secret)){
            result.put("corpId",corpId);
            result.put("secret",secret);
            result.put("agentId",agentId);
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
    public ResponseBo updateCorpInfo(String corpId,String secret,String agentId){
        try {
            qywxService.updateCorpInfo(corpId, secret,agentId);
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
        SysInfoBo qywx = commonFunService.getSysInfoByCode(CommonConstant.QYWX_CODE);
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

    /**
     * 获取小程序ID
     */
    @GetMapping("/getAppID")
    public ResponseBo getAppID(){
        String appId = qywxService.getMpAppId();
        Map<String,Object> result= Maps.newHashMap();
        result.put("appId",appId);
        return ResponseBo.ok(result);
    }

    /**
     * 更新小程序ID
     */
    @PostMapping("/setMpAppId")
    public ResponseBo setMpAppId(String mpappid){
        try {
            qywxService.setMpAppId(mpappid);
            return ResponseBo.ok();
        }catch (Exception e){
            return ResponseBo.error();
        }
    }

    /**
     * 获取是否开启欢迎语
     */
    @GetMapping("/getEnableWel")
    public ResponseBo getEnableWel(){
        String status = qywxService.getEnableWelcome();
        Map<String,Object> result= Maps.newHashMap();
        result.put("status",status);
        return ResponseBo.ok(result);
    }

    /**
     * 设置欢迎语状态
     */
    @PostMapping("/setEnableWelcome")
    public ResponseBo setEnableWelcome(String status){
        try {
            qywxService.setEnableWelcome(status);
            return ResponseBo.ok();
        }catch (Exception e){
            return ResponseBo.error();
        }
    }

    @PostMapping("/saveFile")
    public ResponseBo saveFile(String title,String content){
        try {
            qywxService.saveFile(title,content);
            return ResponseBo.ok();
        }catch (Exception e){
            return ResponseBo.error();
        }
    }
}
