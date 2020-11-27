package com.linksteady.qywx.utils.json;

import com.alibaba.fastjson.JSONObject;


public class JsonHelper {

    public static String getString(JSONObject json, String property) {
        return json.getString(property);
    }

    public static Long getLong(JSONObject json, String property) {
        return json.getLong(property);
    }

    public static Integer getInteger(JSONObject json, String property) {
        return json.getInteger(property);
    }

    public static Double getDouble(JSONObject json, String property) {
        return json.getDouble(property);
    }

}
