package com.linksteady.common.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
public class MenuBo implements java.io.Serializable{

    @Id
    @Column(name = "MENU_ID",insertable = false)
    private Long menuId;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @Column(name = "MENU_NAME")
    private String menuName;

    @Column(name = "URL")
    private String url;

    @Column(name = "PERMS")
    private String perms;

    @Column(name = "ICON")
    private String icon;

}
