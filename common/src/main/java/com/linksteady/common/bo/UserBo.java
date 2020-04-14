package com.linksteady.common.bo;

import com.linksteady.common.domain.Menu;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.domain.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 封装 用户放入shiro session中的安全对象
 */
@Data
public class UserBo implements Serializable {

    private Long userId;

    private String username;

    private String email;

    private String mobile;

    private String firstLogin ="Y";

    private Set<String> permission;
    private Map<String, Tree<Menu>> userMenuTree;

    public UserBo(User user) {
        this.userId = user.getUserId();
        this.username=user.getUsername();
        this.email=user.getEmail();
        this.mobile=user.getMobile();
        this.permission=user.getPermissionSet();
        this.userMenuTree=user.getUserMenuTree();
        this.firstLogin=getFirstLogin();
    }
}