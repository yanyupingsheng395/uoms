package com.linksteady.qywx.domain;

import com.alibaba.fastjson.JSONObject;
import com.linksteady.qywx.utils.json.JsonHelper;
import lombok.Data;

import java.io.Serializable;

/**
 * 第三方单点登录后获取用户信息
 */
@Data
public class UserInfoSso implements Serializable {

    private String corpId;
    private String userType;
    private String userId;
    private String avatar;

    public UserInfoSso buildFromJsonObject(JSONObject jsonObject)
    {
        if(null!=jsonObject)
        {
            this.setUserType(JsonHelper.getString(jsonObject,"usertype"));
            JSONObject userInfo=jsonObject.getJSONObject("user_info");
            JSONObject corpInfo=jsonObject.getJSONObject("corp_info");

            this.setCorpId(JsonHelper.getString(corpInfo,"corpid"));
            this.setUserId(JsonHelper.getString(userInfo,"userid"));
            this.setAvatar(JsonHelper.getString(userInfo,"avatar"));

        }
        return this;
    }

}
