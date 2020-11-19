package com.linksteady.qywx.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2020/9/22
 */
@Data
public class QywxDeptUser {
    private String deptId;
    private String deptName;
    private String deptParentId;
    private String userId;
    private String userName;
}
