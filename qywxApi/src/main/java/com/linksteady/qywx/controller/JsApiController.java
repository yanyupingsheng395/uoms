package com.linksteady.qywx.controller;

import com.google.common.collect.Maps;
import com.linksteady.common.controller.BaseController;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.qywx.service.QywxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

/**
 * jsapi相关的接口
 */
@RestController
@Slf4j
public class JsApiController extends BaseController {

    @Autowired
    QywxService qywxService;

    /**
     * 获取jsapi相关的配置信息
     */
    @RequestMapping("/jsapi/getJsapiInfo")
    public ResponseBo getJsapiInfo(HttpServletRequest httpServletRequest)
    {
        Map<String,String> result= null;
        try {
            String corpId=qywxService.getCorpId();
            //获取当前应用的agentId
//            String agentId=qywxService.getAgentId(corpId);
//            String jsapiTicket=qywxService.getJsapiTicket(corpId);

            String timestamp = Long.toString(System.currentTimeMillis() / 1000);
            String nonceStr=getNoncestr();
            String url=httpServletRequest.getParameter("url");

           // String content="jsapi_ticket="+jsapiTicket+"&noncestr="+nonceStr+"&timestamp="+timestamp+"&url="+url;
            String content="noncestr="+nonceStr+"&timestamp="+timestamp+"&url="+url;
            String signature= SHA1.gen(content);

            result = Maps.newHashMap();
            result.put("nonceStr",nonceStr);
            result.put("timestamp",timestamp);
            result.put("corpId",corpId);
            result.put("signature",signature);

            //获取应用的ticket
           // String agentTicket=qywxService.getAgentJsapiTicket(corpId,agentId);
            //String agentContent="jsapi_ticket="+agentTicket+"&noncestr="+nonceStr+"&timestamp="+timestamp+"&url="+url;
            String agentContent="noncestr="+nonceStr+"&timestamp="+timestamp+"&url="+url;
            String agentSignature=SHA1.gen(agentContent);

            //result.put("agentId",agentId);
            result.put("agentSignature",agentSignature);

            return ResponseBo.okWithData("",result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseBo.error();
        }
    }

    private String getNoncestr() {
        return UUID.randomUUID().toString();
    }
}
