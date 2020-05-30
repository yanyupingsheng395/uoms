package com.linksteady.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hxcao
 * @date 2019-12-07
 */
@Data
public class Ztree implements Serializable {

    private long id;

    private long pId;

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
