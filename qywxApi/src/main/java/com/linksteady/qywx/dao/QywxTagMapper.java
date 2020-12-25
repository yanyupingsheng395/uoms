package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxTag;
import com.linksteady.qywx.domain.QywxTagGroup;

import java.util.List;

public interface QywxTagMapper {
    List<QywxTagGroup> getTagGroup();

    int queryGroupTag( String groupName);

    void addTagGroup(QywxTagGroup qywxTagGroup);

    void addTag(QywxTag qywxTag);

    int queryTag(String tagName, String groupid);
}
