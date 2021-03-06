package com.linksteady.qywx.service;

import com.linksteady.qywx.domain.QywxTag;
import com.linksteady.qywx.domain.QywxTagGroup;
import com.linksteady.qywx.exception.WxErrorException;

import java.util.List;

public interface QywxTagService {
    /**
     * 获取标签组的集合
     * @return
     */
    List<QywxTagGroup> selectTagGroupList(int limit,int offset);

    /**
     * 查看标签组ID合标签名称是否存在
     * @param groupName
     * @return
     */
    int isTagGroupExists( String groupName);

    /**
     * 查询该标签组下面是否存在这个名字的标签
     * @param tagName  新增标签名称
     * @param groupid   标签组ID
     * @return
     */
    int isTagExists(String tagName, String groupid);

    /**
     * 新增标签组
     * @param groupTagName  标签名称
     * @param groupName     标签组名称
     */
    void addTagGroup(String groupTagName, String groupName) throws WxErrorException;

    /**
     * 在标签组下新增标签
     * @param tagName
     * @param groupid
     */
    void addTag(String tagName, String groupid) throws WxErrorException;
    /**
     *修改标签或者标签组名称，都可调用这一个方法
     * @param id   标签ID/标签组ID
     * @param name  新标签名称/新标签组名称
     * @param flag  G：修改标签组/T:修改标签
     */
    void updateGroupTagName(String id, String name,String flag) throws Exception;

    /**
     * 获取标签组的数量
     * @return
     */
    int getTagGroupCount();

    /**
     * 获取该标签组下的标签数量
     * @param groupId
     * @return
     */
    int getTagCount(String groupId);

    /**
     * 分页获取该标签组下的所有标签
     * @param limit
     * @param offset
     * @param groupId
     * @return
     */
    List<QywxTag> getTagGroupDetail(int limit, int offset, String groupId);
    /**
     * 删除标签组或者标签
     * @param id   标签组ID/标签ID
     * @param flag  G：修改标签组/T:修改标签
     * @param groupId  这个groutId为了判断标签组下的标签是否均被删除，如果都被删除，那么也删除该标签组
     * @return
     */
    void delGroupTag(String id, String flag,String groupId) throws Exception;

    /**
     * 从微信端获取所有标签信息
     */
    void syncQywxTagList() throws WxErrorException;

    /**
     *删除标签或者标签组
     */
    void delTagGroupData(String id, String flag) throws WxErrorException;

    /**
     *通过tagID查询当前标签信息
     * @param tagid
     * @return
     */
    QywxTag getTagDetails(String tagid) throws WxErrorException;

    /**
     * 根据事件返回过来的tagid，更新标签
     * @param id
     */
    void updateTag(String id) throws WxErrorException;

    /**
     * 据事件返回过来的tagid，保存标签
     * @param id
     */
    void saveTag(String id) throws WxErrorException;

    /**
     * 为通过欢迎我进来的用户打上标签
     */
    void tagToUserByState(String followUserId,String externalUserId,String state) throws Exception;

    /**
     * 封装为企业成员打标签的通用方法
     * @param followUserId
     * @param externalUserId
     * @param addTagsList
     * @param removeTagsList
     */
    void markCorpTags(String followUserId,String externalUserId,List<String> addTagsList,List<String> removeTagsList) throws WxErrorException;
}
