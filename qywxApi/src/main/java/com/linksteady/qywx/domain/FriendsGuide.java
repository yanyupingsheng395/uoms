package com.linksteady.qywx.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 朋友圈方案
 */
@Data
public class FriendsGuide implements Serializable {

    private String guideDate;

    private String guideName;

    private String pushProduct;

    private String pushTag;

    private String knowHow;

    public FriendsGuide(String guideDate, String guideName, String pushProduct, String pushTag, String knowHow) {
        this.guideDate = guideDate;
        this.guideName = guideName;
        this.pushProduct = pushProduct;
        this.pushTag = pushTag;
        this.knowHow = knowHow;
    }
}
