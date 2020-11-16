package com.linksteady.qywx.controller;

import com.linksteady.common.util.IPUtils;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.qywx.service.ParamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author huang
 * api部分的基础接口
 * api规范，第一个参数为签名  第二个参数为时间戳(秒) 剩下的为业务参数；
 * 签名的规范为 所有业务参数按先后顺拼成字符串，然后sha1
 */
public class VerifyController {

    /**
     * api调用的时间差 60秒
     */
    private static final long TIMESTAMP_DFF=60;

    @Autowired
    ParamService paramService;

    protected void validateLegality(HttpServletRequest request,String signature,String...args) throws Exception
    {
        //默认第一个参数为时间戳
        String timestamp=args[0];

        //获取当前的时间戳
        long now=LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8));
        if(now-Long.parseLong(timestamp)>TIMESTAMP_DFF)
        {
            throw new Exception("参数错误!");
        }

        //判断签名
        String sign= SHA1.gen(args);
        if(!sign.equals(signature))
        {
            throw new Exception("签名错误，请校验数据!");
        }
        //判断IP地址 如果IP地址配置了且不是* 则判断当前IP是否在白名单内，如果不在，抛出异常
       String allowAddress= paramService.getQywxDomainAddress();
       if(StringUtils.isNotEmpty(allowAddress)&&!"*".equals(allowAddress))
       {
           String currentIp=IPUtils.getIpAddr(request);
           if(!allowAddress.contains(currentIp))
           {
               throw new Exception("当前IP不在可调用白名单之内!");
           }
       }
    }

}
