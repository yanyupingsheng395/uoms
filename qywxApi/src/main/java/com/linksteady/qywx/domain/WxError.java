package com.linksteady.qywx.domain;


import com.alibaba.fastjson.JSONObject;
import com.linksteady.qywx.utils.json.WxErrorAdapter;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 微信错误码.
 * 请阅读：
 * 企业微信：<a href="https://work.weixin.qq.com/api/doc#10649">全局错误码</a>
 */
@Data
@Builder
public class WxError implements Serializable {
    private static final long serialVersionUID = 7869786563361406291L;

    /**
     * 微信错误代码.
     */
    private int errorCode;

    /**
     * 微信错误信息.
     * （如果可以翻译为中文，就为中文）
     */
    private String errorMsg;

    /**
     * 微信接口返回的错误原始信息（英文）.
     */
    private String errorMsgEn;

    private String json;

    public static WxError getWxError(int errorCode,String errorMsg)
    {
        WxError.WxErrorBuilder errorBuilder = WxError.builder();
        errorBuilder.errorCode(errorCode);
        errorBuilder.errorMsg(errorMsg);
        return  errorBuilder.build();
    }

    public static WxError fromJsonObject(JSONObject jsonObject) {
        final WxError wxError = WxErrorAdapter.deserialize(jsonObject);
        if (wxError.getErrorCode() == 0) {
            return wxError;
        }

        if (StringUtils.isNotEmpty(wxError.getErrorMsg())) {
            wxError.setErrorMsgEn(wxError.getErrorMsg());
        }

        final String msg = WxErrorMsgEnum.findMsgByCode(wxError.getErrorCode());
        if (msg != null) {
            wxError.setErrorMsg(msg);
        }

        return wxError;
    }

    @Override
    public String toString() {
        if (this.json == null) {
            return "错误代码：" + this.errorCode + ", 错误信息：" + this.errorMsg;
        }

        return "错误代码：" + this.errorCode + ", 错误信息：" + this.errorMsg + "，微信原始报文：" + this.json;
    }

}
