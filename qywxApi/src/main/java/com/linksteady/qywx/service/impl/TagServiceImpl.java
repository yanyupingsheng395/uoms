package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.TagMapper;
import com.linksteady.qywx.domain.QywxTag;
import com.linksteady.qywx.domain.TagGroup;
import com.linksteady.qywx.domain.WxError;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.service.TagService;
import com.linksteady.qywx.utils.TimeStampUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户标签管理的服务类
 */
@Service
@Slf4j
public class TagServiceImpl implements TagService {

    @Autowired
    QywxService qywxService;

    @Autowired
    TagMapper tagMapper;

    @Override
    public List<QywxTag> selectTagList(String corpId) {
        return tagMapper.selectTagList(corpId);
    }

    @Override
    @Transactional
    public synchronized void syncTags(String corpId) throws WxErrorException {
        StringBuffer requestUrl=new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_CORP_TAG_LIST));
        requestUrl.append("?access_token="+qywxService.getAccessToken());

        log.info("同步客户标签，请求的url:{}",requestUrl.toString());
        String tagResult= OkHttpUtil.postRequest(requestUrl.toString(),"");
        JSONObject jsonObject = JSON.parseObject(tagResult);
        log.info("同步客户标签接口返回的结果为{}",jsonObject);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }
        JSONArray groupArray=jsonObject.getJSONArray("tag_group");

        //删除所有的标签
        tagMapper.deleteAllTags(corpId);

        List<QywxTag> list= Lists.newArrayList();
        QywxTag qywxTag=null;
        JSONObject group=null;
        JSONObject tag=null;
        JSONArray tagArray=null;
        if(null!=groupArray&&groupArray.size()>0)
        {
           log.info("group的数量:{}",groupArray.size());
           for(int i=0;i<groupArray.size();i++)
           {
               group=groupArray.getJSONObject(i);
               tagArray=group.getJSONArray("tag");

               log.info("{}下标签的数量:{}",group.getString("group_name"),tagArray.size());
               for(int j=0;j<tagArray.size();j++)
               {
                   tag=tagArray.getJSONObject(j);
                   qywxTag=new QywxTag();
                   qywxTag.setCorpId(corpId);
                   qywxTag.setInsertDt(LocalDateTime.now());
                   qywxTag.setGroupId(group.getString("group_id"));
                   qywxTag.setGroupName(group.getString("group_name"));
                   qywxTag.setGroupCreateTime(TimeStampUtils.timeStampToDate(String.valueOf(group.getLongValue("create_time"))));
                   qywxTag.setGroupOrder(group.getIntValue("order"));

                   qywxTag.setTagId(tag.getString("id"));
                   qywxTag.setTagName(tag.getString("name"));
                   qywxTag.setTagCreateTime(TimeStampUtils.timeStampToDate(String.valueOf(tag.getLongValue("create_time"))));
                   qywxTag.setTagOrder(tag.getIntValue("order"));

                   list.add(qywxTag);
               }
           }
        }

        log.info("list的数量为{}",list.size());
        //对标签进行保存
        if(list.size()>0)
        {
            int pagesize=100;
            int pagenum = list.size() % pagesize == 0 ? list.size() / pagesize : (list.size() / pagesize) + 1;
            for(int m=0;m<pagenum;m++)
            {
                List<QywxTag> temp = list.stream().skip(pagesize * m).limit(pagesize).collect(Collectors.toList());
                log.info("执行标签的保存操作");
                tagMapper.saveTags(temp);
            }

        }
    }

    @Override
    public List<TagGroup> selectTagGroupList(String corpId) {
        return tagMapper.selectTagGroupList(corpId);
    }
}
