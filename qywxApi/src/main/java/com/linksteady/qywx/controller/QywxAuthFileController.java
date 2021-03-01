package com.linksteady.qywx.controller;

import com.linksteady.qywx.domain.QywxParam;
import com.linksteady.qywx.service.QywxParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class QywxAuthFileController {

    @Autowired
    QywxParamService qywxParamService;

    /**
     * 企业微信授权文件
     */
    @org.springframework.web.bind.annotation.RequestMapping("/{authFileName}.txt")
    @ResponseBody
    public String qywxAuthFile(@PathVariable String authFileName) {
        QywxParam qywxParam=qywxParamService.getQywxParam();

        if(null==qywxParam||StringUtils.isEmpty(qywxParam.getOauthFilename())||!qywxParam.getOauthFilename().equals(authFileName))
        {
            return "校验文件不存在!";
        }else
        {
            return null==qywxParam.getOauthFile()?"校验文件不存在":qywxParam.getOauthFile();
        }
    }

}
