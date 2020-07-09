package com.linksteady.qywx.controller;

import com.linksteady.common.util.IPUtils;
import com.linksteady.qywx.crypto.SHA1;
import com.linksteady.qywx.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author huang
 * api部分的基础接口
 * api规范，第一个参数为签名  第二个参数为时间戳(秒) 剩下的为业务参数；
 * 签名的规范为 所有业务参数按先后顺拼成字符串，然后sha1
 */
public class ApiBaseController {

    /**
     * api调用的时间差 60秒
     */
    private static final long TIMESTAMP_DFF=60;

    @Autowired
    ApiService apiService;

    protected String validateLegality(HttpServletRequest request,String signature,String...args)
    {
        //默认第一个参数为时间戳
        String timestamp=args[0];
        //判断时间戳
        //获取当前的时间戳
        long now=LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8));
        if(now-Long.parseLong(timestamp)>TIMESTAMP_DFF)
        {
            return "参数错误，请稍后再试!";
        }

        //判断签名
        String sign= SHA1.gen(args);
        if(!sign.equals(signature))
        {
            return "签名错误，请校验数据!";
        }
        //判断IP地址
       String allowAddress=apiService.getQywxDomainAddress();
       if(StringUtils.isEmpty(allowAddress)||"*".equals(allowAddress))
       {
           //放行
           return null;
       }else
       {
           String currentIp=IPUtils.getIpAddr(request);
           if(allowAddress.contains(currentIp))
           {
               return null;
           }else
           {
               return "当前IP不在可调用白名单之内!";
           }
       }
    }


    public static void main(String[] args) {
        test("1", "2", "3");
    }

    public static void test(String... args) {
        for (String i:
            args ) {
            System.out.println(i);
        }
    }

}
