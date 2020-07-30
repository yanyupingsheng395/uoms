package com.linksteady.qywx.controller;

import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.qywx.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
public class ApiCommonController {

    @Autowired
    ApiService apiService;

    private static final String EXPIRE_OPERATE_INFO="/api/expireOperateInfo";

    /**
     * 刷新企业微信端当前企业缓存的用户成长系统地址信息
     */
    @RequestMapping("/flushRemoteOperateInfo")
    public ResponseBo flushRemoteOperateInfo() {
        String corpId=apiService.getQywxCorpId();
        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
        String signature= SHA1.gen(timestamp);
        //2.提交到企业微信端
        StringBuffer url=new StringBuffer(apiService.getQywxDomainUrl()+EXPIRE_OPERATE_INFO);
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

}
