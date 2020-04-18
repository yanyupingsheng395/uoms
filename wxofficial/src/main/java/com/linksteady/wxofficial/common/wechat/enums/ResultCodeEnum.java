package com.linksteady.wxofficial.common.wechat.enums;

import lombok.Data;

/**
 * @author hxcao
 * @date 2020/4/17
 */
public enum ResultCodeEnum {
    /**
     * 200
     */
    RES_200("正常数据", "200");
    public String name;
    public String code;
    ResultCodeEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
