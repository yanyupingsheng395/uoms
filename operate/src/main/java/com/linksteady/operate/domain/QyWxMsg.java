package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/5/15
 */
@Data
public class QyWxMsg {
    private long qywxId;
    private String textContent;
    private Date insertDt;
    private String insertBy;
    private Date updateDt;
    private Date updateBy;
    private String productUrl;
    private String productName;
    private String couponUrl;
    private String couponName;
    private String isPersonal;
    private String materialType;
    private String materialContent;
}
