package com.linksteady.qywx.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class QywxTag {
    /**
     * 标签ID
     */
    private String tagId;
    /**
     * 标签名称
     */
    private String tagName;
    /**
     * 标签创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date tagCreateTime;
    /**
     * 标签次序
     */
    private long tagOrder;
    /**
     * 标签组ID
     */
    private String groupId;
    /**
     * 新增时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date insertDt;
    /**
     * 新增人
     */
    private String insertBy;
}
