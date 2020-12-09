package com.linksteady.qywx.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class QywxMediaImg implements Serializable {
    /**
     * 主键ID
     */
    private long imgId;
    /**
     * 图片内容
     */
    private byte[] imgContent;
    /**
     * 新增时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime insertDt;
    /**
     * 新增人
     */
    private String insertBy;
    /**
     * 图片标题
     */
    private String imgTitle;
    /**
     * 图片media_id
     */
    private String mediaId;
    /**
     * 媒介失效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime mediaExpireDate;
    /**
     * 业务标记ID
     */
    private int identityId;
    /**
     * 业务标记类型，prod表示商品，coupon表示优惠券
     */
    private String  indetityType;
}
