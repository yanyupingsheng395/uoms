package com.linksteady.qywx.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class QywxTag {

    private String groupId;

    private String groupName;

    private Date groupCreateTime;

    private int groupOrder;

    private String tagId;

    private String tagName;

    private Date tagCreateTime;

    private int tagOrder;

    private LocalDateTime insertDt;

    private String corpId;
}
