package com.linksteady.qywx.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QywxTagGroup {
    /**
     * 标签组ID
     */
    private String  groupId;
    /**
     * 标签组名称
     */
    private String groupName;

    /**
     * 标签组次序
     */
    private long groupOrder;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date createTime;
    /**
     * 标签组集合
     */
    private List<QywxTag> tagList;
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
