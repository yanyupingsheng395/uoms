package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.QywxTagMapper;
import com.linksteady.qywx.domain.QywxTag;
import com.linksteady.qywx.domain.QywxTagGroup;
import com.linksteady.qywx.domain.WxError;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.service.QywxTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<QywxTagGroup> getTagList() {
        List<QywxTagGroup> tagList=qywxTagMapper.getTagGroup();
        return tagList;
    }

    @Override
    public int queryGroupTag(String groupName) {
        return qywxTagMapper.queryGroupTag(groupName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTagGroup(String groupTagName, String groupName) throws WxErrorException {
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
            qywxTagMapper.addTagGroup(qywxTagGroup);
            JSONArray tagarr = tag_group.getJSONArray("tag");
            if(tagarr.size()>0){
                JSONObject object = tagarr.getJSONObject(0);
                qywxTag.setTagId(object.getString("id"));
                qywxTag.setGroupId(tag_group.getString("group_id"));
                qywxTag.setTagCreateTime(new Date());
                qywxTag.setTagName(groupTagName);
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
    public void addTag(String tagName, String groupid) throws WxErrorException {
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
                    qywxTag.setTagOrder(object.getIntValue("order"));
                    qywxTagMapper.addTag(qywxTag);
                }
            }
        }
    }
}
