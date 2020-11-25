package com.linksteady.qywx.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class QywxContactWayDetail {
    /**
     * 主键ID
     */
   private Long contactWayDetailId;
    /**
     * 联系我主表ID
     */
   private Long contactWayId;
    /**
     * 目标ID
     */
   private String objId;
    /**
     * 目标名称
     */
   private String objName;
    /**
     * 类型  U表示用户 D表示部门
     */
   private String objType;
    /**
     * 写入时间
     */
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
   private Date insertDt;
}
