package com.linksteady.wxofficial.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/4/15
 */
@Data
public class ImageText {

    private int id;

    private String title;

    private String userGroup;

    private String author;

    private byte[] content;

    private byte[] cover;

    private String wxAbstract;

    private String contentStr;

    private String createBy;
    private Date createDt;
    private String updateBy;
    private Date updateDt;

    public ImageText() {
    }

    public ImageText(String title, String userGroup) {
        this.title = title;
        this.userGroup = userGroup;
    }
}
