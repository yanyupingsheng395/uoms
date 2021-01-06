package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.QywxTagMapper;
import com.linksteady.qywx.domain.QywxTag;
import com.linksteady.qywx.domain.QywxTagGroup;
import com.linksteady.qywx.domain.WxError;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.service.QywxTagService;
import com.linksteady.qywx.utils.TimeStampUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class QywxTagServiceImpl implements QywxTagService {

    @Autowired(required = false)
    private QywxTagMapper qywxTagMapper;

    @Autowired
    private QywxService qywxService;

    @Override
    public List<QywxTagGroup> getTagList(int limit,int offset) {
        List<QywxTagGroup> tagList=qywxTagMapper.getTagGroup(limit,offset);
        return tagList;
    }

    @Override
    public int queryGroupTag(String groupName) {
        return qywxTagMapper.queryGroupTag(groupName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void addTagGroup(String groupTagName, String groupName) throws WxErrorException {
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.ADD_CORP_TAG));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());
        List<Map<String,Object>> list= Lists.newArrayList();
        Map<String,Object> tag= Maps.newHashMap();
        tag.put("name",groupTagName);
        list.add(tag);
        JSONObject param= new JSONObject();
        param.put("group_name",groupName);
        param.put("tag",list);
        String detailData = OkHttpUtil.postRequestByJson(requestUrl.toString(), JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(detailData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }
        JSONObject tag_group = jsonObject.getJSONObject("tag_group");
        QywxTagGroup qywxTagGroup=new QywxTagGroup();
        QywxTag qywxTag=new QywxTag();
        if(tag_group!=null){
            qywxTagGroup.setGroupId(tag_group.getString("group_id"));
            qywxTagGroup.setGroupName(groupName);
            qywxTagGroup.setGroupOrder(tag_group.getIntValue("order"));
            qywxTagGroup.setCreateTime(new Date());
            qywxTagGroup.setInsertDt(new Date());
            qywxTagGroup.setInsertBy((((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername()));
            qywxTagMapper.addTagGroup(qywxTagGroup);
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

    @Override
    public int queryTag(String tagName, String groupid) {
        return qywxTagMapper.queryTag(tagName,groupid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void addTag(String tagName, String groupid) throws WxErrorException {
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.ADD_CORP_TAG));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());
        List<Map<String,Object>> list= Lists.newArrayList();
        Map<String,Object> tag= Maps.newHashMap();
        tag.put("name",tagName);
        list.add(tag);
        JSONObject param= new JSONObject();
        param.put("group_id",groupid);
        param.put("tag",list);
        String detailData = OkHttpUtil.postRequestByJson(requestUrl.toString(), JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(detailData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }
        JSONObject tag_group = jsonObject.getJSONObject("tag_group");
        QywxTag qywxTag=new QywxTag();
        if(tag_group!=null){
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
                    qywxTagMapper.addTag(qywxTag);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void updateGroupTagName(String id, String name,String flag) throws Exception {
        if("G".equals(flag)){
            qywxTagMapper.updateTagGroup(id,name);
        }else if("T".equals(flag)){
            qywxTagMapper.updateTag(id,name);
        }else{
            throw  new Exception("错误类型，不能更新数据库");
        }
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.EDIT_CORP_TAG));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());
        JSONObject param= new JSONObject();
        param.put("id",id);
        param.put("name",name);
        String resultData = OkHttpUtil.postRequestByJson(requestUrl.toString(), JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(resultData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }
    }

    @Override
    public int getGroupTagCount() {
        return qywxTagMapper.getGroupTagCount();
    }

    @Override
    public int getTagCount(String groupId) {
        return qywxTagMapper.getTagCount(groupId);
    }

    @Override
    public List<QywxTag> getTagGroupDetail(int limit, int offset, String groupId) {
        return qywxTagMapper.getTagGroupDetail(limit,offset,groupId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void delGroupTag(String id, String flag,String groupId) throws Exception {
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

        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.DEL_CORP_TAG));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());
        JSONObject param= new JSONObject();
        List<String> list= Arrays.asList(id);
        if("G".equals(flag)){
            param.put("group_id",list);
        }else if("T".equals(flag)){
            param.put("tag_id",list);
        }
        String resultData = OkHttpUtil.postRequestByJson(requestUrl.toString(), JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(resultData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncQywxTagList() throws WxErrorException {
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_CORP_TAG_LIST));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());
        List<String> list= Arrays.asList();
        JSONObject param= new JSONObject();
        param.put("tag_id",list);
        String resultData = OkHttpUtil.postRequestByJson(requestUrl.toString(),JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(resultData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }
        //清空标签组和标签的所有数据
        qywxTagMapper.delAllTagGroup();
        qywxTagMapper.delAllTag();
        //组织数据
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
}
