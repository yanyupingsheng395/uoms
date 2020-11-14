package com.linksteady.qywx.utils.json;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.qywx.domain.WxError;
import org.apache.commons.lang3.StringUtils;

public class WxErrorAdapter {

    public static WxError deserialize(JSONObject json) {
        WxError.WxErrorBuilder errorBuilder = WxError.builder();

        if (StringUtils.isNotEmpty(json.getString("errcode"))) {
            errorBuilder.errorCode(json.getIntValue("errcode"));
        }
        if (StringUtils.isNotEmpty(json.getString("errmsg"))) {
            errorBuilder.errorMsg(json.getString("errmsg"));
        }

        errorBuilder.json(json.toString());

        return errorBuilder.build();
    }

}
