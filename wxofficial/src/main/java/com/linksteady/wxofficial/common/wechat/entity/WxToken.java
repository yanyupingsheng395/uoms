package com.linksteady.wxofficial.common.wechat.entity;

import lombok.Data;

/**
 * 微信获取到的token
 * @author hxcao
 * @date 2020/4/15
 */
@Data
public class WxToken {

    private String accessToken;

    private String appId;

    private String funcInfo;
}
