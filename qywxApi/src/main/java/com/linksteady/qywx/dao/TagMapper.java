package com.linksteady.qywx.dao;

import com.linksteady.common.config.MyMapper;
import com.linksteady.qywx.domain.QywxTag;
import com.linksteady.qywx.domain.TagGroup;

import java.util.List;

public interface TagMapper extends MyMapper<QywxTag> {

    /**
     * 返回标签列表
     * @param corpId
     * @return
     */
    List<QywxTag> selectTagList(String corpId);

    /**
     * 保存客户标签
     */
    void saveTags(List<QywxTag> qywxTagList);

    /**
     * 删除当前企业所有的标签
     */
    void deleteAllTags(String corpId);

    List<TagGroup> selectTagGroupList(String corpId);

}
