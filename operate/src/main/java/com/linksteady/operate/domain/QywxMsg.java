package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/5/15
 */
@Data
public class QywxMsg {
    private Long qywxId;
    private String textContent;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
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
    private String usedDays;
}
