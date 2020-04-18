package com.linksteady.wxofficial.common.wechat.enums;

/**
 * @author hxcao
 * @date 2020/4/17
 */
public enum MediaTypeEnum {
    /**
     * 媒体类型
     */
    NEWS("图文消息", "news"), IMAGE("图片", "image");
    public String name;
    public String code;

    MediaTypeEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
