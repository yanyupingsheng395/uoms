package com.linksteady.operate.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QywxTagGroup {
    /**
     * 标签ID
     */
    private long tagId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 群名称
     */
    private String groupName;
    /**
     * 发送邀请成员
     */
    private String sendInviteMembers;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date insertDt;
    /**
     * 已邀请客户
     */
    private long inviteMembers;
    /**
     * 以入群客户
     */
    private long joinMembers;
    /**
     * 未发送成员
     */
    private long NoSendMembers;
    /**
     * 未邀请成员
     */
    private long noInviteMembers;
}
