package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.FollowUserMapper;
import com.linksteady.qywx.dao.QywxChatMapper;
import com.linksteady.qywx.domain.*;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxChatService;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.utils.TimeStampUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QywxChatServiceImpl implements QywxChatService {

    @Autowired(required = false)
    private QywxChatMapper qywxChatMapper;
    @Autowired
    private QywxService qywxService;
    @Autowired(required = false)
    private FollowUserMapper followUserMapper;


    @Override
    public int getCount(String owner,String status) {
        return qywxChatMapper.getCount(owner,status);
    }

    @Override
    public List<QywxChatBase> getDataList(int limit, int offset,String owner,String status) {
        return qywxChatMapper.getDataList(limit, offset,owner,status);
    }

    @Override
    public int getCustomerListCount(String chatId) {
        return qywxChatMapper.getCustomerListCount(chatId);
    }

    @Override
    public List<QywxChatDetail> getCustomerList(int limit, int offset, String chatId) {
        return qywxChatMapper.getCustomerList(limit, offset,chatId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncQywxChatList() throws WxErrorException {
        //获取所有的导购
        List<String> followUserList=
                followUserMapper.getFollowUserList().stream().map(i->i.getUserId()).collect(Collectors.toList());

        if(followUserList.size()<=100)
        {
            syncGroupChart("",followUserList);
        }else
        {
            //按100个导购一次进行遍历 计算切分次数
            int limit=followUserList.size()%100==0?followUserList.size()/100:followUserList.size()/100+1;
            for(int i=0;i<limit;i++)
            {
                syncGroupChart("",followUserList.stream().skip(i*100).limit(100).collect(Collectors.toList()));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delChatBase(String chatId) {
        qywxChatMapper.deleteChatBase(chatId);
        qywxChatMapper.deleteChatDetail(chatId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChat(String chatId) throws WxErrorException {
        this.saveChatInfo(chatId,null);
    }

    @Override
    public QywxChatBase getChatBaseDetail(String chatId) {
        return qywxChatMapper.getChatBaseDetail(chatId);
    }

    /**
     * 获取客户群列表
     * @param cursor
     */
    public void syncGroupChart(String cursor,List<String> followUserList) throws WxErrorException
    {
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_GROUPCHAT_LIST));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());

        JSONObject param= new JSONObject();
        param.put("limit",100);
        if(!StringUtils.isEmpty(cursor)){
            param.put("cursor",cursor);
        }
        JSONObject ownerList=new JSONObject();
        ownerList.put("userid_list",followUserList);
        param.put("owner_filter",ownerList);

        log.info("获取客户群列表参数{}",JSON.toJSONString(param));
        String detailData = OkHttpUtil.postRequestByJson(requestUrl.toString(),JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(detailData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }

        JSONArray array = jsonObject.getJSONArray("group_chat_list");
        JSONObject chatObj=null;

        for (int i = 0; i < array.size(); i++) {
            chatObj=array.getJSONObject(i);
            saveChatInfo(chatObj.getString("chat_id"),chatObj.getString("status"));
        }
        cursor=jsonObject.getString("next_cursor");
        //如果分页游标有值，那么接着往下查。
        if(StringUtils.isNotEmpty(cursor)){
            syncGroupChart(cursor,followUserList);
        }
    }

    @Override
    public List<QywxChatStatistics> getDetailData(String chatId) {
        return qywxChatMapper.getDetailData(chatId);
    }


    /**
     * 根据客户群ID，获取客户群详情及群成员列表并保存
     * @param chatId
     * @param status
     * @return
     * @throws WxErrorException
     */
    private void saveChatInfo(String chatId,String status) throws WxErrorException {
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_GROUPCHAT_GET));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());

        JSONObject param= new JSONObject();
        param.put("chat_id",chatId);
        String detailData = OkHttpUtil.postRequestByJson(requestUrl.toString(),JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(detailData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }

        JSONObject chatObject = jsonObject.getJSONObject("group_chat");
        QywxChatBase qywxChatBase=null;
        List<QywxChatDetail> list=Lists.newArrayList();
        if(chatObject!=null){
            //将数据存在客户群明细表
            JSONArray array = chatObject.getJSONArray("member_list");

            qywxChatBase=new QywxChatBase();
            qywxChatBase.setChatId(chatId);
            if(StringUtils.isNotEmpty(status))
            {
                qywxChatBase.setStatus(status);
            }
            qywxChatBase.setGroupName(chatObject.getString("name"));
            qywxChatBase.setNotice(chatObject.getString("notice"));
            qywxChatBase.setOwner(chatObject.getString("owner"));
            qywxChatBase.setCreateTime(TimeStampUtils.timeStampToDate(chatObject.getString("create_time")));
            qywxChatBase.setGroupNumber(array.size());

            //遍历客户群成员，并存入uo_qywx_chat_detail表
            if(array.size()>0){
                for (int i = 0; i < array.size(); i++) {
                    QywxChatDetail detail = new QywxChatDetail();
                    JSONObject object = array.getJSONObject(i);
                    detail.setUserId(object.getString("userid"));
                    detail.setUserType(object.getString("type"));
                    detail.setChatId(chatId);
                    detail.setJoinScene(object.getString("join_scene"));
                    detail.setJoinTime(TimeStampUtils.timeStampToDate(object.getString("join_time")));
                    list.add(detail);
                }
            }
            qywxChatMapper.insertChatBase(qywxChatBase);
            //插入客户群明细表
            if(list.size()>0){
                qywxChatMapper.insertDetail(list);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncChatStatistics() {
        //初始化数据。参照uo_qywx_chat_base插入日期和群聊ID
        qywxChatMapper.insertChatDay();

        //更新群总人数和新增人数
        List<QywxChatBase> baselist=qywxChatMapper.getChatBaseData();
        baselist.stream().forEach(x->{

            Table<String,Long,Object> qywxChatTable =  HashBasedTable.create();
            String chatId = x.getChatId();
            List<QywxChatStatisticsVO> list=qywxChatMapper.getChatSummary(chatId);
            //查的群明细有值，才进行计算；
            if(list.size()>0){
                list.stream().forEach(y->{
                    qywxChatTable.put(y.getChatId(),y.getJoinDay(),y);
                });

                //计算总人数和新增人数
                List <QywxChatStatistics> chatStatistics=qywxChatMapper.getChatStatisticsById(chatId);
                for (int i = 0; i < chatStatistics.size(); i++) {
                    QywxChatStatistics current = chatStatistics.get(i);
                    //根据查询统计表的chatID和日期，去table集合中找
                    QywxChatStatisticsVO vo =(QywxChatStatisticsVO) qywxChatTable.get(current.getChatId(), current.getDayWid());
                    if(vo!=null){
                        current.setGroupNumber(vo.getGrandTotalCnt());
                        current.setAddNumber(vo.getCnt());
                    }else{
                        current.setAddNumber(0);
                        current.setGroupNumber(chatStatistics.get(i-1).getGroupNumber());
                    }

                    //计算退群人数
                    if(i==0){
                        current.setOutNumber(0);
                    }else{
                        QywxChatStatistics pre = chatStatistics.get(i - 1);
                        if(current.getChatId().equals(pre.getChatId())){
                            current.setOutNumber(current.getGroupNumber()-pre.getGroupNumber()-current.getAddNumber());
                        }else{
                            //如果不是，那么就是新的chatID，退群人数就为0
                            current.setOutNumber(0);
                        }
                    }
                }
                qywxChatMapper.updateNumber(chatStatistics);
            }
        });
    }

    @Override
    public FriendsNumVO getFriendsNum(String chatId) {
        return qywxChatMapper.getFriendsNum(chatId);
    }

    public static void main(String[] args) {
        List<QywxChatStatisticsVO> list=new ArrayList<>();
        list.stream().forEach(x->{
            Table<String,Long,Object> qywxChatTable =  HashBasedTable.create();
            qywxChatTable.put(x.getChatId(),x.getJoinDay(),x);
        });
    }

}
