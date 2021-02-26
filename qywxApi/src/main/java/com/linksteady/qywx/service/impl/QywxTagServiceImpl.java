package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.QywxContactWayMapper;
import com.linksteady.qywx.dao.QywxTagMapper;
import com.linksteady.qywx.domain.QywxTag;
import com.linksteady.qywx.domain.QywxTagGroup;
import com.linksteady.qywx.domain.WxError;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.service.QywxTagService;
import com.linksteady.qywx.utils.TimeStampUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 标签组管理
 */
@Service
@Slf4j
public class QywxTagServiceImpl implements QywxTagService {

    @Autowired(required = false)
    private QywxTagMapper qywxTagMapper;

    @Autowired
    private QywxService qywxService;

    @Autowired(required = false)
    private QywxContactWayMapper qywxContactWayMapper;

    /**
     * 公共方法，获取标签组的集合
     */
    @Override
    public List<QywxTagGroup> getTagList(int limit,int offset) {
        List<QywxTagGroup> tagList=qywxTagMapper.getTagGroup(limit,offset);
        return tagList;
    }
    /**
     * 查看标签组ID合标签名称是否存在
     * @param groupName
     * @return
     */
    @Override
    public int queryGroupTag(String groupName) {
        return qywxTagMapper.queryGroupTag(groupName);
    }

    /**
     * 新增标签组
     * @param groupTagName  标签名称
     * @param groupName     标签组名称
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void addTagGroup(String groupTagName, String groupName) throws WxErrorException {
        //企业微信接口
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.ADD_CORP_TAG));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());

        //拼装请求数据
        List<Map<String,Object>> list= Lists.newArrayList();
        Map<String,Object> tag= Maps.newHashMap();
        tag.put("name",groupTagName);
        list.add(tag);
        JSONObject param= new JSONObject();
        param.put("group_name",groupName);
        param.put("tag",list);

        //请求微信接口，并解析返回状态码
        log.info("新增标签组拼接参数：{}", JSON.toJSONString(param));
        String detailData = OkHttpUtil.postRequestByJson(requestUrl.toString(), JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(detailData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            log.info("新增标签组异常：{}", detailData);
            throw new WxErrorException(error);
        }

        //解析微信返回标签数据，存入数据库
        JSONObject tag_group = jsonObject.getJSONObject("tag_group");
        QywxTagGroup qywxTagGroup=new QywxTagGroup();
        QywxTag qywxTag=new QywxTag();
        if(tag_group!=null){

            //解析标签组信息，并存入数据库
            qywxTagGroup.setGroupId(tag_group.getString("group_id"));
            qywxTagGroup.setGroupName(groupName);
            qywxTagGroup.setGroupOrder(tag_group.getIntValue("order"));
            qywxTagGroup.setCreateTime(new Date());
            qywxTagGroup.setInsertDt(new Date());
            qywxTagGroup.setInsertBy((((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername()));
            qywxTagMapper.addTagGroup(qywxTagGroup);

            //解析标签信息，并存入数据库
            JSONArray tagarr = tag_group.getJSONArray("tag");
            if(tagarr.size()>0){
                JSONObject object = tagarr.getJSONObject(0);
                qywxTag.setTagId(object.getString("id"));
                qywxTag.setGroupId(tag_group.getString("group_id"));
                qywxTag.setTagCreateTime(new Date());
                qywxTag.setInsertDt(new Date());
                qywxTag.setTagName(groupTagName);
                qywxTag.setInsertBy((((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername()));
                qywxTag.setTagOrder(object.getIntValue("order"));
                qywxTagMapper.addTag(qywxTag);
            }
        }
    }
    /**
     * 查询该标签组下面是否存在这个名字的标签
     * @param tagName  新增标签名称
     * @param groupid   标签组ID
     * @return
     */
    @Override
    public int queryTag(String tagName, String groupid) {
        return qywxTagMapper.queryTag(tagName,groupid);
    }

    /**
     * 在标签组下新增标签
     * @param tagName
     * @param groupid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void addTag(String tagName, String groupid) throws WxErrorException {
        //企业微信接口
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.ADD_CORP_TAG));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());

        //拼装请求数据
        List<Map<String,Object>> list= Lists.newArrayList();
        Map<String,Object> tag= Maps.newHashMap();
        tag.put("name",tagName);
        list.add(tag);
        JSONObject param= new JSONObject();
        param.put("group_id",groupid);
        param.put("tag",list);

        //请求微信接口，并解析返回状态码
        log.info("在标签组下新增标签拼接参数：{}", JSON.toJSONString(param));
        String detailData = OkHttpUtil.postRequestByJson(requestUrl.toString(), JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(detailData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            log.info("在标签组下新增标签拼接参数异常：{}",detailData);
            throw new WxErrorException(error);
        }

        //解析微信返回标签数据，存入数据库
        JSONObject tag_group = jsonObject.getJSONObject("tag_group");
        QywxTag qywxTag=new QywxTag();
        if(tag_group!=null){

            //解析标签的信息
            JSONArray tagarr = tag_group.getJSONArray("tag");
            if(tagarr.size()>0){
                JSONObject object = tagarr.getJSONObject(0);
                if(object.size()>0){
                    qywxTag.setTagId(object.getString("id"));
                    qywxTag.setGroupId(groupid);
                    qywxTag.setTagCreateTime(new Date());
                    qywxTag.setTagName(tagName);
                    qywxTag.setInsertDt(new Date());
                    qywxTag.setInsertBy((((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername()));
                    qywxTag.setTagOrder(object.getIntValue("order"));
                    //将标签信息存入数据库
                    qywxTagMapper.addTag(qywxTag);
                }
            }
        }
    }

    /**
     *修改标签或者标签组名称，都可调用这一个方法
     * @param id   标签ID/标签组ID
     * @param name  新标签名称/新标签组名称
     * @param flag  G：修改标签组/T:修改标签
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void updateGroupTagName(String id, String name,String flag) throws Exception {
        //根据flag类型，判断是修改标签组名称还是修改标签名称
        if("G".equals(flag)){
            qywxTagMapper.updateTagGroup(id,name);
        }else if("T".equals(flag)){
            qywxTagMapper.updateTag(id,name);
        }else{
            throw  new Exception("错误类型，不能更新数据库");
        }

        //企业微信接口
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.EDIT_CORP_TAG));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());

        //拼装数据
        JSONObject param= new JSONObject();
        param.put("id",id);
        param.put("name",name);

        //请求微信接口。解析状态码
        log.info("修改标签或者标签组名称,拼装参数：{}",JSON.toJSONString(param));
        String resultData = OkHttpUtil.postRequestByJson(requestUrl.toString(), JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(resultData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            log.info("修改标签或者标签组名称错误：{}",resultData);
            throw new WxErrorException(error);
        }
    }
    /**
     * 获取标签组的数量
     * @return
     */
    @Override
    public int getTagGroupCount() {
        return qywxTagMapper.getTagGroupCount();
    }

    /**
     * 获取该标签组下的标签数量
     */
    @Override
    public int getTagCount(String groupId) {
        return qywxTagMapper.getTagCount(groupId);
    }

    /**
     * 分页获取该标签组下的所有标签
     */
    @Override
    public List<QywxTag> getTagGroupDetail(int limit, int offset, String groupId) {
        return qywxTagMapper.getTagGroupDetail(limit,offset,groupId);
    }

    /**
     * 删除标签组或者标签
     * @param id   标签组ID/标签ID
     * @param flag  G：修改标签组/T:修改标签
     * @param groupId  这个groutId为了判断标签组下的标签是否均被删除，如果都被删除，那么也删除该标签组
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void delGroupTag(String id, String flag,String groupId) throws Exception {
        //根据flag类型，判断是删除标签组还是删除标签,并删除数据库中内容
        if("G".equals(flag)){
            //删除标签组
            qywxTagMapper.delGroupTag(id);
            //删除标签组下的所有标签
            qywxTagMapper.delTagByGroupId(id);
        }else if("T".equals(flag)){
            //根据标签ID，删除
            qywxTagMapper.delTagByTagId(id);
            int count = qywxTagMapper.getTagCount(groupId);
            if(count==0){
                //如果一个标签组下所有的标签均被删除，则标签组会被自动删除。
                qywxTagMapper.delGroupTag(groupId);
            }
        }else{
            throw  new Exception("错误类型，不能更新数据库");
        }

        //企业微信接口
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.DEL_CORP_TAG));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());

        //根据flag类型，组装请求数据
        JSONObject param= new JSONObject();
        List<String> list= Arrays.asList(id);
        if("G".equals(flag)){
            param.put("group_id",list);
        }else if("T".equals(flag)){
            param.put("tag_id",list);
        }

        //请求微信接口，并解析数据
        log.info("删除标签组或者标签拼接参数：{}",JSON.toJSONString(param));
        String resultData = OkHttpUtil.postRequestByJson(requestUrl.toString(), JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(resultData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            log.info("删除标签组或者标签错误：{}",resultData);
            throw new WxErrorException(error);
        }
    }


    /**
     * 从微信端获取所有标签信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncQywxTagList() throws WxErrorException {
        //企业微信接口
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_CORP_TAG_LIST));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());

        //组装数据
        List<String> list= Lists.newArrayList();
        JSONObject param= new JSONObject();
        param.put("tag_id",list);

        //获取请求数据，解析状态码
        log.info("从微信端获取所有标签信息，拼装的参数:{}",JSON.toJSONString(param));
        String resultData = OkHttpUtil.postRequestByJson(requestUrl.toString(),JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(resultData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            log.info("从微信端获取所有标签信息错误:{}",resultData);
            throw new WxErrorException(error);
        }

        //解析微信返回的标签组信息。
        JSONArray jsonArray = JSON.parseArray(jsonObject.getString("tag_group"));
        List<QywxTagGroup> tagGroupList=Lists.newArrayList();
        if(jsonArray.size()>0){
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                //组装标签组数据
                QywxTagGroup qywxTagGroup=new QywxTagGroup();
                if(object!=null){
                    qywxTagGroup.setGroupId(object.getString("group_id"));
                    qywxTagGroup.setGroupName(object.getString("group_name"));
                    qywxTagGroup.setGroupOrder(object.getIntValue("order"));
                    qywxTagGroup.setCreateTime(TimeStampUtils.timeStampToDate(object.getString("create_time")));
                    qywxTagGroup.setInsertDt(new Date());
                    qywxTagGroup.setInsertBy((((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername()));

                    //获取标签组下面标签内容
                    JSONArray tagArray = JSON.parseArray(object.getString("tag"));
                    List<QywxTag> tagList=Lists.newArrayList();
                    if(tagArray.size()>0){
                        for (int j = 0; j < tagArray.size(); j++) {
                            JSONObject tagObject = tagArray.getJSONObject(j);
                            if(tagObject!=null){
                                QywxTag qywxTag=new QywxTag();
                                qywxTag.setInsertBy(((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername());
                                qywxTag.setInsertDt(new Date());
                                qywxTag.setTagOrder(tagObject.getIntValue("order"));
                                qywxTag.setTagName(tagObject.getString("name"));
                                qywxTag.setTagCreateTime(TimeStampUtils.timeStampToDate(object.getString("create_time")));
                                qywxTag.setGroupId(object.getString("group_id"));
                                qywxTag.setTagId(tagObject.getString("id"));
                                tagList.add(qywxTag);
                            }
                        }
                    }
                    //将子标签的数据插入数据库
                    qywxTagMapper.addTagList(tagList);
                    //将标签组的信息，存进集合中
                    tagGroupList.add(qywxTagGroup);
                }
            }
            qywxTagMapper.addTagGroupList(tagGroupList);
        }

    }

    /**
     *删除标签或者标签组
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delTagGroupData(String id, String flag) throws WxErrorException {
        //根据标签ID，删除
        qywxTagMapper.delTagByTagId(id);
        QywxTag qywxTag = getTagDetails(id);
        if(qywxTag!=null){
            String groupId = qywxTag.getGroupId();
            int count = qywxTagMapper.getTagCount(groupId);
            if(count==0){
                //如果一个标签组下所有的标签均被删除，则标签组会被自动删除。
                qywxTagMapper.delGroupTag(groupId);
            }
        }
    }

    /**
     * 通过tagID查询当前标签信息，用于应对事件变更回调微信端只返回tagdid
     * @param tagid
     */
    @Override
    public QywxTag getTagDetails(String tagid) throws WxErrorException {
        //企业微信接口
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_CORP_TAG_LIST));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());

        //组装请求数据
        List<String> list= Lists.newArrayList();
        list.add(tagid);
        JSONObject param= new JSONObject();
        param.put("tag_id",list);

        //请求微信接口，并解析状态码
        log.info("通过tagID查询当前标签信息:{}",JSON.toJSONString(param));
        String resultData = OkHttpUtil.postRequestByJson(requestUrl.toString(),JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(resultData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            log.info("通过tagID查询当前标签信息,微信端返回数据错误:{}",resultData);
            throw new WxErrorException(error);
        }

        //解析微信段返回的标签组数据
        JSONArray jsonArray = JSON.parseArray(jsonObject.getString("tag_group"));
        QywxTag qywxTag=new QywxTag();
        if(jsonArray.size()>0){
            JSONObject object = jsonArray.getJSONObject(0);
            if(object!=null){
                JSONObject tagObject = object.getJSONArray("tag").getJSONObject(0);
                if(tagObject!=null){
                    qywxTag.setTagId(tagid);
                    qywxTag.setTagName(tagObject.getString("name"));
                    qywxTag.setTagCreateTime(TimeStampUtils.timeStampToDate(tagObject.getString("create_time")));
                    qywxTag.setTagOrder(tagObject.getIntValue("order"));
                    qywxTag.setGroupId(object.getString("group_id"));
                }
            }
        }
        return qywxTag;
    }

    /**
     * 根据事件返回过来的tagid，更新标签
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTag(String id) throws WxErrorException {
        QywxTag qywxTag = getTagDetails(id);
        if(qywxTag!=null){
            qywxTagMapper.updateTag(id,qywxTag.getTagName());
        }
    }

    /**
     * 据事件返回过来的tagid，保存标签
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTag(String id) throws WxErrorException {
        QywxTag qywxTag = getTagDetails(id);
        if (qywxTag != null) {
            qywxTagMapper.addTag(qywxTag);
        }
    }

    /**
     * 为通过欢迎我进来的用户打上标签
     */
    @Override
    public void tagToUserByState(String followUserId, String externalUserId, String state) throws Exception{
        //判断当前stage是否配置有标签
        String tagIds=qywxContactWayMapper.getTagIdsByState(state);

        //如果标签不为空，则进行打标签操作
        if(!StringUtils.isEmpty(tagIds)){
            List<String> addTagsList= Splitter.on(',').trimResults().omitEmptyStrings().splitToList(tagIds);
            markCorpTags(followUserId,externalUserId,addTagsList,null);
        }
    }

    /**
     * 封装为企业成员打标签的通用方法
     */
    @Override
    public void markCorpTags(String followUserId, String externalUserId, List<String> addTagsList, List<String> removeTagsList) throws WxErrorException{
        //企业微信接口
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.MARK_CORP_TAG));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());

        //组装请求数据
        Map<String,Object> param=Maps.newHashMap();
        param.put("userid",followUserId);
        param.put("external_userid",externalUserId);
        if(null!=addTagsList&& addTagsList.size()>0){
            param.put("add_tag",addTagsList);
        }
        if(null!=removeTagsList&& removeTagsList.size()>0){
            param.put("remove_tag",removeTagsList);
        }

        log.info("为用户打标签，传入的参数为:{}",JSON.toJSONString(param));
        String result = OkHttpUtil.postRequestByJson(requestUrl.toString(),JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(result);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }
    }


}
