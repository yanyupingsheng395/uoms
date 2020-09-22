package com.linksteady.operate.domain;

import lombok.Data;

import java.util.Date;

/**
 * @author hxcao
 * @date 2020/9/22
 */
@Data
public class QywxFollowuserList {
    private String userId;
    private String userName;
    private Date insertDt;
    private String insertBy;
    private Date updateDt;
    private String updateBy;
    private String corpId;
    private String flag;
    private long deptId;
}
