package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.QywxTag;
import com.linksteady.qywx.domain.TagGroup;
import com.linksteady.qywx.exception.WxErrorException;

import java.util.List;

public interface TagService {

    /**
     * 查询本地的客户标签
     */
    List<QywxTag> selectTagList(String corpId);

    /**
     * 同步客户标签
     * @param corpId
     */
    void syncTags(String corpId)  throws WxErrorException;

    List<TagGroup> selectTagGroupList(String corpId);
}
