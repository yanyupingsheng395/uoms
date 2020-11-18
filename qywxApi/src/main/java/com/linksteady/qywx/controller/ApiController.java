package com.linksteady.qywx.controller;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.qywx.service.QywxGropMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
public class ApiController {


    private static final String EXPIRE_OPERATE_INFO="/api/expireOperateInfo";

    private static final String OPEN_WEICOME="/api/openWelcome";

    private static final String CLOSE_WEICOME="/api/closeWelcome";

    @Autowired
    QywxGropMsgService qywxGropMsgService;

    /**
     * 打开欢迎语
     */
    @RequestMapping("/openWelcome")
    public ResponseBo openWelcome() {
        String corpId= "";
        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
        String signature= SHA1.gen(timestamp);
        //2.提交到企业微信端
        StringBuffer url=new StringBuffer(""+OPEN_WEICOME);
        url.append("?corpId="+corpId);
        url.append("&timestamp="+timestamp);
        url.append("&signature="+signature);
        OkHttpUtil.getRequest(url.toString());
        return ResponseBo.ok();
    }

    /**
     * 关闭欢迎语
     */
    @RequestMapping("/closeWelcome")
    public ResponseBo closeWelcome() {
        String corpId= "";
        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
        String signature= SHA1.gen(timestamp);
        //2.提交到企业微信端
        StringBuffer url=new StringBuffer(""+CLOSE_WEICOME);
        url.append("?corpId="+corpId);
        url.append("&timestamp="+timestamp);
        url.append("&signature="+signature);
        OkHttpUtil.getRequest(url.toString());
        return ResponseBo.ok();
    }

    /**
     * 返回接口应用的状态
     */
    @RequestMapping("/status")
    public ResponseBo status() {
        return ResponseBo.ok("up");
    }

    @PostMapping("/addMsgTemplate")
    public String addMsgTemplate(String addparam){
       return qywxGropMsgService.addMsgTemplate( JSONObject.parseObject(addparam));
    }

}
