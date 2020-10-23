package com.linksteady.qywx.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class WxProductMedia  implements Serializable {

    /**
     * 商品ID
     */
    private long productId;
    /**
     * 商品图片地址
     */
    private String prodPicUrl;
    /**
     * 地址类型，HTTP表示http_url
     */
    private String urlType;
}
