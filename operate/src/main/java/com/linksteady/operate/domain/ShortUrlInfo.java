package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author admin
 * @date 2019-10-24
 */
@Data
public class ShortUrlInfo {

    private Long id;

    private String  longUrl;

    private String shortUrl;

    private Date validateDate;

    private Date insertDt;

    private String insertBy;

    private String sourceType;

    private String validFlag;

}
