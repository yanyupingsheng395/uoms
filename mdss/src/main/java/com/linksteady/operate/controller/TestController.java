package com.linksteady.operate.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.io.ByteStreams;
import com.linksteady.operate.domain.JobBo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Decoder;
import javax.servlet.http.HttpServletRequest;
import java.io.*;

/**
 * @author hxcao
 * @date 2019-07-11e
 */
@RestController
public class TestController {

    @PostMapping("/test")
    public void test(HttpServletRequest request) throws IOException, ClassNotFoundException {
        request.setCharacterEncoding("utf-8");
        String json = new String(ByteStreams.toByteArray(request.getInputStream()));
        JobBo jobBo = JSONObject.toJavaObject(JSONObject.parseObject(json), JobBo.class);
        String authorization = request.getHeader("Authorization");
        String token=new String(new BASE64Decoder().decodeBuffer(authorization.split(" ")[1]));
        System.out.println(token);
    }
}
