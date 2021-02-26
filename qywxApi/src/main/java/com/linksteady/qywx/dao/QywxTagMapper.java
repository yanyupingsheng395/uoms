package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxTag;
import com.linksteady.qywx.domain.QywxTagGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QywxTagMapper {
    List<QywxTagGroup> getTagGroup(int limit,int offset);
    List<QywxTag> selectTag();

    int queryGroupTag( String groupName);

    void addTagGroup(QywxTagGroup qywxTagGroup);

    void addTag(QywxTag qywxTag);

    int queryTag(String tagName, String groupid);

    void updateTagGroup(String id, String name);

    void updateTag(String id, String name);

    int getTagGroupCount();

    int getTagCount(String groupId);

    List<QywxTag> getTagGroupDetail(int limit, int offset, String groupId);

    void delGroupTag(String id);

    void delTagByGroupId(String id);

    void delTagByTagId(String id);

    void addTagList(@Param("tagList") List<QywxTag> list);

    void addTagGroupList(@Param("tagGroupList")List<QywxTagGroup> list);

}
