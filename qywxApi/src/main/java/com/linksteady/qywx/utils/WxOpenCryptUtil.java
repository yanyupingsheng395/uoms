package com.linksteady.qywx.utils;

import com.linksteady.common.util.crypto.WxCryptUtil;
import com.linksteady.qywx.service.QywxService;
import org.apache.commons.codec.binary.Base64;

public class WxOpenCryptUtil extends WxCryptUtil {
    /**
     * 构造函数
     *
     */
    public WxOpenCryptUtil(String corpId,String token,String aesKey) {
        this.token = token;
        this.corpId = corpId;
        this.aesKey = Base64.decodeBase64(aesKey + "=");
    }
}
