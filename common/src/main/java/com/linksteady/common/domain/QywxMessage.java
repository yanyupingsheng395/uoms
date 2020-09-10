package com.linksteady.common.domain;

import lombok.Data;

@Data
public class QywxMessage {

    /**
     * 文本
     */
    private String text;

    /**
     * 图片相关
     */
    private String imgMediaId;
    private String imgPicUrl;

    /**
     * 链接
     */
    private String linkTitle;
    private String linkPicUrl;
    private String linkDesc;
    private String linkUrl;

    /**
     * 小程序
     */
    private String mpTitle;
    private String mpPicMediaId;
    private String mpAppid;
    private String mpPage;

}
