package com.linksteady.wxofficial.entity.po;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/4/30
 */
@Data
public class WxPushDetail {

    private int id;

    private int headId;

    private String openId;

    private Date pushDate;

    private String pushStatus;

    private Date updateDt;
}
