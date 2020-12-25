package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.QywxTagGroup;
import com.linksteady.qywx.exception.WxErrorException;

import java.util.List;

public interface QywxTagService {
    List<QywxTagGroup> getTagList();

    /**
     * 查看标签组ID合标签名称是否存在
     * @param groupName
     * @return
     */
    int queryGroupTag( String groupName);

    /**
     * 新增标签组
     * @param groupTagName  标签名称
     * @param groupName     标签组名称
     */
    void addTagGroup(String groupTagName, String groupName) throws WxErrorException;

    /**
     * 查询该标签组下面是否存在这个名字的标签
     * @param tagName  新增标签名称
     * @param groupid   标签组ID
     * @return
     */
    int queryTag(String tagName, String groupid);

    /**
     * 在标签组下新增标签
     * @param tagName
     * @param groupid
     */
    void addTag(String tagName, String groupid) throws WxErrorException;
}
