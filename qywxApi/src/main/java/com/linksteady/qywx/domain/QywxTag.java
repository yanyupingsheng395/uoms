package com.linksteady.qywx.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

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
}
