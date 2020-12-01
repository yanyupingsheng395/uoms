package com.linksteady.common.bo;

import com.linksteady.common.domain.MenuBo;
import com.linksteady.common.domain.Tree;
import com.linksteady.common.domain.User;
import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Set;

/**
 * 封装 用户放入shiro session中的安全对象
 */
@Data
public class UserBo implements Serializable {

    @Id
    private Long userId;

    private String username;

    private String email;

    private String mobile;

    private String firstLogin ="Y";

    private Tree<MenuBo> userMenuTree;

    public UserBo(User user) {
        this.userId = user.getUserId();
        this.username=user.getUsername();
        this.email=user.getEmail();
        this.mobile=user.getMobile();
        this.userMenuTree=user.getUserMenuTree();
        this.firstLogin=getFirstLogin();
        this.firstLogin=user.getFirstLogin();
    }
}
