package com.linksteady.qywx.domain;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.qywx.utils.json.JsonHelper;
import lombok.Data;

/**
 * 第三方通过oauth授权后获取用户信息
 */
@Data
public class UserInfoThirdParty {

    private Integer errcode;
    private String errmsg;

    private String userId;

    private String corpId;

    private String deviceId;

    private String userTicket;

    private Long expiresIn;

    private String openUserId;

    public UserInfoThirdParty buildFromJsonObject(JSONObject jsonObject)
    {
        if(null!=jsonObject)
        {
           this.setErrcode(JsonHelper.getInteger(jsonObject,"errcode"));
           this.setErrmsg(JsonHelper.getString(jsonObject,"errmsg"));
           this.setUserId(JsonHelper.getString(jsonObject,"UserId"));
           this.setCorpId(JsonHelper.getString(jsonObject,"CorpId"));
           this.setDeviceId(JsonHelper.getString(jsonObject,"DeviceId"));
           this.setUserTicket(JsonHelper.getString(jsonObject,"user_ticket"));
           this.setExpiresIn(JsonHelper.getLong(jsonObject,"expires_in"));
           this.setOpenUserId(JsonHelper.getString(jsonObject,"open_userid"));
        }
        return this;
    }
}
