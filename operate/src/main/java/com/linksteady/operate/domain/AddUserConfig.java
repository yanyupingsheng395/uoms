package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/7/16
 */
@Data
public class AddUserConfig {
    private long id;
    private long headId;
    private String sendType;
    private String userValue;
    private String lifeCycle;
    private String pathActive;
    private String userGrowth;
    private String addUserMethod;
    private long isChannel;
    private long isProduct;
    private long isCoupon;
    private long isQrcode;
    private String qrId;
    private String smsContent;
}
