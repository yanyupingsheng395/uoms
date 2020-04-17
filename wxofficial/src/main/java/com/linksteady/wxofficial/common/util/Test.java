package com.linksteady.wxofficial.common.util;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.wxofficial.common.wechat.entity.WxToken;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;

/**
 * @author hxcao
 * @date 2020/4/15
 */
public class Test {

    public static void main(String[] args) {
        String token = OkHttpUtil.callTextPlain("http://wx.growth-master.com/api/get_authorizer_info?appId=wxa18e9ed9f8a213d8");
        System.out.println(token);

        JSONObject jsonObject = JSONObject.parseObject(token);
        String code = jsonObject.getString("code");
        if(StringUtils.isNotEmpty(code) && "200".equalsIgnoreCase(code)) {
            String msgInfo = jsonObject.getString("msg");
            final WxToken wxToken = JSONObject.parseObject(msgInfo).toJavaObject(WxToken.class);
        }
    }
}
