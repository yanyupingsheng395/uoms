package com.linksteady.qywx.domain;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.qywx.utils.json.JsonHelper;
import lombok.Data;

/**
 * 第三方通过oauth授权后获取用户敏感信息
 */
@Data
public class UserDetailThirdParty {
    private Integer errcode;
    private String errmsg;
    private String userId;
    private String corpId;
    private String name;
    private String gender;
    private String avatar;
    private String qrCode;

    public UserDetailThirdParty buildFromJsonObject(JSONObject jsonObject)
    {
        if(null!=jsonObject)
        {
            this.setErrcode(JsonHelper.getInteger(jsonObject,"errcode"));
            this.setErrmsg(JsonHelper.getString(jsonObject,"errmsg"));
            this.setCorpId(JsonHelper.getString(jsonObject,"corpid"));
            this.setUserId(JsonHelper.getString(jsonObject,"userid"));
            this.setName(JsonHelper.getString(jsonObject,"name"));
            this.setGender(JsonHelper.getString(jsonObject,"gender"));
            this.setAvatar(JsonHelper.getString(jsonObject,"avatar"));
            this.setQrCode(JsonHelper.getString(jsonObject,"qr_code"));
        }
        return this;
    }

}
