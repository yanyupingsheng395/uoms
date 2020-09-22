package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/9/22
 */
@Data
public class QywxDept {
    private long id;
    private String name;
    private long parentId;
    private long orderNo;
    private Date insertDt;
    private Date updateDt;
    private String enabled;
    private String corpId;
    private String insertBy;
    private String updateBy;
}
