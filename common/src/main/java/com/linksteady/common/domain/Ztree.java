package com.linksteady.common.domain;

import lombok.Data;

/**
 * @author hxcao
 * @date 2019-12-07
 */
@Data
public class Ztree {

    private String id;

    private String pId;

    private String name;

    private boolean open;

    /**
     * 判断是否为父节点
     */
    private boolean isParent;

    public void setIsParent(boolean isParent) {
        this.isParent = isParent;
    }

    public boolean getIsParent() {
        return isParent;
    }
}
