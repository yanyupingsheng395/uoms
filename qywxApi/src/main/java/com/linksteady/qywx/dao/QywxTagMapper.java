package com.linksteady.qywx.dao;

import com.linksteady.qywx.domain.QywxTag;
import com.linksteady.qywx.domain.QywxTagGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QywxTagMapper {

    List<QywxTagGroup> selectTagGroupList(int limit,int offset);

    List<QywxTag> selectTag();

    int isTagGroupExists( String groupName);

    int isTagExists(String tagName, String groupid);

    void addTagGroup(QywxTagGroup qywxTagGroup);

    void addTag(QywxTag qywxTag);

    void updateTagGroup(String id, String name);

    void updateTag(String id, String name);

    int getTagGroupCount();

    int getTagCount(String groupId);

    List<QywxTag> getTagGroupDetail(int limit, int offset, String groupId);

    void delTagGroup(String id);

    void delTagByGroupId(String id);

    void delTagByTagId(String id);

    void addTagList(@Param("tagList") List<QywxTag> list);

    void addTagGroupList(@Param("tagGroupList")List<QywxTagGroup> list);

}
