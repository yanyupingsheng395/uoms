package com.linksteady.common.bo;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-06-27
 */
@Data
public class UserRoleBo {

    private String roleId;

    private String userId;

    private String userName;

    private String hasPermission;
}
