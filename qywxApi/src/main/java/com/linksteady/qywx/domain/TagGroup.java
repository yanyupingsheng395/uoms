package com.linksteady.qywx.domain;

import com.linksteady.qywx.domain.QywxTag;

import java.util.List;

public class TagGroup {

    private String groupId;

    private String groupName;

    private List<QywxTag> qywxTagList;


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<QywxTag> getQywxTagList() {
        return qywxTagList;
    }

    public void setQywxTagList(List<QywxTag> qywxTagList) {
        this.qywxTagList = qywxTagList;
    }
}
