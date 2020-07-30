package com.linksteady.qywx.domain;

import lombok.Data;

import java.util.Date;

@Data
public class SyncTask {

    private Long taskId;

    private String corpId;

    private String  taskType;

    private String status;

    private Date insertDt;

    private String insertBy;

    private Date updateDt;

    private String updateBy;

    public SyncTask() {
    }

    public SyncTask(String corpId, String taskType, String status, Date insertDt, String insertBy) {
        this.corpId = corpId;
        this.taskType = taskType;
        this.status = status;
        this.insertDt = insertDt;
        this.insertBy = insertBy;
    }
}
