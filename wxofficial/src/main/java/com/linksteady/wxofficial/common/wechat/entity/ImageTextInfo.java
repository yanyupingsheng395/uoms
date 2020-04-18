package com.linksteady.wxofficial.common.wechat.entity;

import lombok.Data;

/**
 * @author hxcao
 * @date 2020/4/17
 */
@Data
public class ImageTextInfo {

    private String thumbMediaId;
    private String thumbUrl;
    private String author;
    private String title;
    private String contentSourceUrl;
    private String content;
    private String digest;
    private boolean showCoverPic;
    private String url;
    private Boolean needOpenComment;
    private Boolean onlyFansCanComment;
}
