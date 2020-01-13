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

    /**
     * 长链接
     */
    private String  longUrl;

    /**
     * 短链接
     */
    private String shortUrl;

    /**
     * 有效日期截止
     */
    private Date validateDate;

    private Date insertDt;

    private String insertBy;

    /**
     * SOURCE_TYPE 生成来源 S表示系统调用  M表示界面调用
     */
    private String sourceType;

    public ShortUrlInfo(String longUrl, String shortUrl, Date validateDate, String sourceType) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        this.validateDate = validateDate;
        this.sourceType = sourceType;
    }
}
