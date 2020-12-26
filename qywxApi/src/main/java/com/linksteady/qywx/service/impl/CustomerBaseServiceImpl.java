package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.CustomerBaseMapper;
import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.domain.QywxChatDetail;
import com.linksteady.qywx.domain.QywxChatBase;
import com.linksteady.qywx.domain.WxError;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.CustomerBaseService;
import com.linksteady.qywx.service.QywxService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CustomerBaseServiceImpl implements CustomerBaseService {

    @Autowired
    private CustomerBaseMapper customerBaseMapper;
    @Autowired
    private QywxService qywxService;

    @Override
    public int getCount() {
        return customerBaseMapper.getCount();
    }

    @Override
    public List<QywxChatBase> getDataList(int limit, int offset) {
        return customerBaseMapper.getDataList(limit, offset);
    }

    @Override
    public int getCustomerListCount(String chatId) {
        return customerBaseMapper.getCustomerListCount(chatId);
    }

    @Override
    public List<QywxChatDetail> getCustomerList(int limit, int offset, String chatId) {
        return customerBaseMapper.getCustomerList(limit, offset,chatId);
    }

    @Override
    public List<FollowUser> getFollowUser() {
        return customerBaseMapper.getFollowUser();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getQywxChatList( String cursor) throws WxErrorException {
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_GROUPCHAT_LIST));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());

        JSONObject param= new JSONObject();
        param.put("limit",100);
        if(!StringUtils.isEmpty(cursor)){
            param.put("cursor",cursor);
        }
        String detailData = OkHttpUtil.postRequestByJson(requestUrl.toString(),JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(detailData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }
        JSONArray array = jsonObject.getJSONArray("group_chat_list");
        //客户群列表
        List<QywxChatBase> list= Lists.newArrayList();
        if(array.size()>0){
            for (int i = 0; i < array.size(); i++) {
                JSONObject js=array.getJSONObject(i);
                if(js!=null){
                    QywxChatBase contractList = new QywxChatBase();
                    String chatid = js.getString("chat_id");
                    JSONObject detail = getChatDetail(chatid);
                    if(detail!=null){
                        contractList.setOwner(detail.getString("owner"));
                        contractList.setGroupName(detail.getString("name"));
                        contractList.setCreateTime(new Date(Long.parseLong(detail.getString("createTime"))));
                        contractList.setNotice(detail.getString("notice"));
                        contractList.setGroupNumber(detail.getIntValue("groupNumber"));
                    }
                    contractList.setChatId(chatid);
                    contractList.setStatus(js.getString("status"));
                    list.add(contractList);
                }
            }
        }
        if(list.size()>0){
            //新增客户群主表
            customerBaseMapper.insertChatBase(list);
        }
        cursor=jsonObject.getString("next_cursor");
        //如果分页游标有值，那么接着往下查。
        if(StringUtils.isNotEmpty(cursor)){
            getQywxChatList(cursor);
        }
    }

    /**
     * 根据客户群ID，获取客户群详情，以及客户群列表人员
     * @param chatid
     * @return
     * @throws WxErrorException
     */
    private JSONObject getChatDetail(String chatid) throws WxErrorException {
        JSONObject resultObject =new JSONObject();
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_GROUPCHAT_GET));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());
        JSONObject param= new JSONObject();
        param.put("chat_id",chatid);
        String detailData = OkHttpUtil.postRequestByJson(requestUrl.toString(),JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(detailData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }
        JSONObject chatObject = jsonObject.getJSONObject("group_chat");
        List<QywxChatDetail> list=Lists.newArrayList();
        if(chatObject!=null){
            //将数据存在客户群明细表
            JSONArray array = chatObject.getJSONArray("member_list");
            //客户群详细内容返回
            resultObject.put("name",chatObject.getString("name"));
            resultObject.put("notice",chatObject.getString("notice"));
            resultObject.put("owner",chatObject.getString("owner"));
            resultObject.put("createTime",chatObject.get("create_time"));
            resultObject.put("groupNumber",array.size());
            //遍历客户群成员，并存入uo_qywx_chat_detail表
            if(array.size()>0){
                for (int i = 0; i < array.size(); i++) {
                    QywxChatDetail detail = new QywxChatDetail();
                    JSONObject object = array.getJSONObject(i);
                    detail.setUserId(object.getString("userid"));
                    detail.setUserType(object.getString("type"));
                    detail.setChatId(chatid);
                    detail.setJoinScene(object.getString("join_scene"));
                    detail.setJoinTime(new Date(Long.parseLong(object.getString("join_time"))));
                    list.add(detail);
                }
            }
        }
        //插入客户群明细表
        if(list.size()>0){
            customerBaseMapper.insertDetail(list);
        }
        return resultObject;
    }

}
