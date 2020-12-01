package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.service.impl.BaseService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.ExternalContactMapper;
import com.linksteady.qywx.domain.ExternalContact;
import com.linksteady.qywx.domain.WxError;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.ExternalContactService;
import com.linksteady.qywx.service.FollowUserService;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.utils.TimeStampUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ExternalContactServiceImpl implements ExternalContactService {

    @Autowired
    QywxService qywxService;

    @Autowired
    FollowUserService followUserService;

    @Autowired(required = false)
    ExternalContactMapper externalContactMapper;

    /**
     * 同步外部联系人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncExternalContact() throws Exception
    {
        log.info("同步外部联系人开始");
        //设置当前企业所有外部客户的删除标记为1
        externalContactMapper.updateDeleteFlag();

        //获取配置了客户联系功能的成员列表
        List<String> followerUserList = followUserService.selectFollowUserList();
        for (String followerUserId : followerUserList) {
            //获取此成员下所有的外部客户信息
            List<String> externalUserList = findExternalContractList(followerUserId);

            List<ExternalContact> externalContacts = null;
            //保存所有的外部客户信息
            if (externalUserList.size() > 0) {
                externalContactMapper.saveExternalUserList(followerUserId, externalUserList);

                int count = 1;
                String cursor = "";
                Map<String, Object> result = null;
                //调用获取明细的接口
                while (count == 1 || (count > 1 && StringUtils.isNotEmpty(cursor))) {
                    result = getExternalContractDetailBatch(cursor, followerUserId, externalUserList);
                    cursor = (String) result.get("corsor");
                    externalContacts = (List<ExternalContact>) result.get("externalContactList");
                    externalContactMapper.saveExternalContractBatch(externalContacts);
                    count++;
                }
            }
        }

        //删除
        externalContactMapper.deleteExternalUser();
        log.info("同步外部联系人结束");
    }

    @Override
    public List<String> findExternalContractList(String userId) throws WxErrorException {
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.LIST_EXTERNAL_CONTACT));
        requestUrl.append(userId);
        requestUrl.append("&access_token=" + qywxService.getAccessToken());

        String userInfo = OkHttpUtil.getRequest(requestUrl.toString());
        JSONObject jsonObject = JSON.parseObject(userInfo);
        WxError error = WxError.fromJsonObject(jsonObject);
        //当前员工没有外部联系人
        if(error.getErrorCode()==84061)
        {
            return Lists.newArrayList();
        }
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }
        JSONArray jsonArray = jsonObject.getJSONArray("external_userid");
        return jsonArray.toJavaList(String.class);
    }

    /**
     * 获取单个外部联系人详情
     * @param followerUserId
     * @param externalUserid
     * @return
     * @throws WxErrorException
     */
    @Override
    public ExternalContact getExternalContractDetail(String followerUserId, String externalUserid) throws WxErrorException {
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_CONTACT_DETAIL));
        requestUrl.append(externalUserid);
        requestUrl.append("&access_token=" + qywxService.getAccessToken());

        String userDetail = OkHttpUtil.getRequest(requestUrl.toString());
        JSONObject jsonObject = JSON.parseObject(userDetail);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }

        log.debug("获取到的客户详情为:{}", userDetail);
        ExternalContact externalContact = new ExternalContact().buildFromJsonObjectSingle(jsonObject, followerUserId);
        return externalContact;
    }

    /**
     * 批量获取外部联系人详情
     * @param followerUserId
     * @param externalUserList
     * @return
     * @throws WxErrorException
     */
    @Override
    public Map<String,Object> getExternalContractDetailBatch(String cursor,String followerUserId, List<String> externalUserList) throws WxErrorException {
        StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_CONTACT_DETAIL_BATCH));
        requestUrl.append("?access_token=" + qywxService.getAccessToken());

        Map<String,Object> param= Maps.newHashMap();
        param.put("userid",followerUserId);
        param.put("cursor",cursor);
        param.put("limit",100);

        String detailData = OkHttpUtil.postRequestByJson(requestUrl.toString(),JSON.toJSONString(param));
        JSONObject jsonObject = JSON.parseObject(detailData);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }

        log.debug("获取到的客户详情为:{}", detailData);
        JSONArray jsonArray=jsonObject.getJSONArray("external_contact_list");
        String nextCursor=jsonObject.getString("next_cursor");

        ExternalContact externalContact=null;
        List<ExternalContact> externalContactList=Lists.newArrayList();

        for(int i=0;i<jsonArray.size();i++)
        {
            externalContact = new ExternalContact().buildFromJsonObject(jsonArray.getJSONObject(i), followerUserId);
            externalContactList.add(externalContact);
        }

        Map<String,Object> result=Maps.newHashMap();
        result.put("corsor",nextCursor);
        result.put("externalContactList",externalContactList);

        return result;
    }

    @Override
    public void saveExternalUserId(String followerUserId, String externalUserId) {
        externalContactMapper.saveExternalUserId(followerUserId, externalUserId);
    }

    @Override
    public void updateExternalContract(ExternalContact externalContact) {
        externalContactMapper.updateExternalContract(externalContact);
    }

    @Override
    public void deleteExternalContract(String followerUserId, String externalUserId) {
        externalContactMapper.deleteExternalContract(followerUserId,externalUserId);
    }


    @Override
    public int selectLocalContactCount() {
        return externalContactMapper.selectLocalContactCount();
    }

    @Override
    public List<ExternalContact> selectLocalContractList(int limit, int offset) {
        return externalContactMapper.selectLocalContractList(limit,offset);
    }


    @Override
    public List<ExternalContact> getGuidanceList(int limit,
                                                 int offset,
                                                 String followUserId,
                                                 String relation,
                                                 String loss,
                                                 String stageValue,
                                                 String interval) {
        StringBuffer whereInfo=new StringBuffer("1=1");
        whereInfo.append(String.format("follow_user_id='%s'",followUserId));

        if(StringUtils.isNotEmpty(relation))
        {
            whereInfo.append(String.format("relation='%s'",relation));
        }
        if(StringUtils.isNotEmpty(loss))
        {
            whereInfo.append(String.format("loss='%s'",loss));
        }
        if(StringUtils.isNotEmpty(stageValue))
        {
            whereInfo.append(String.format("stage_value='%s'",stageValue));
        }
        if(StringUtils.isNotEmpty(interval))
        {
            whereInfo.append(String.format("touch_interval='%s'",interval));
        }

        List<ExternalContact> result = externalContactMapper.getGuidanceList(whereInfo.toString(),limit,offset);
        for (ExternalContact externalContact : result) {
            //设置添加时间
            externalContact.setCreateTimeStr(TimeStampUtils.timeStampToDateString(externalContact.getCreatetime()));
            //设置标签
        }
        return result;
    }

    @Override
    public int getGuidanceCount(String followUserId,
                                                 String relation,
                                                 String loss,
                                                 String stageValue,
                                                 String interval) {
        StringBuffer whereInfo=new StringBuffer("1=1");
        whereInfo.append(String.format("follow_user_id='%s'",followUserId));

        if(StringUtils.isNotEmpty(relation))
        {
            whereInfo.append(String.format("relation='%s'",relation));
        }
        if(StringUtils.isNotEmpty(loss))
        {
            whereInfo.append(String.format("loss='%s'",loss));
        }
        if(StringUtils.isNotEmpty(stageValue))
        {
            whereInfo.append(String.format("stage_value='%s'",stageValue));
        }
        if(StringUtils.isNotEmpty(interval))
        {
            whereInfo.append(String.format("touch_interval='%s'",interval));
        }
        return externalContactMapper.getGuidanceCount(whereInfo.toString());
    }

    /**
     * todo 此方法有性能问题，优化；
     * @param followUserId
     * @param addtime
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<ExternalContact> getAddTimeList(String followUserId, String addtime,Integer offset, Integer limit) {
        List<ExternalContact> result=Lists.newArrayList();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //查询结果集
        List<ExternalContact> querydata = externalContactMapper.selectExternalUserList(followUserId,offset,limit);
        //获取当前时间
        LocalDateTime nowtime = LocalDateTime.now();
        for (ExternalContact externalContact : querydata) {
            String createTime=externalContact.getCreatetime();
            if(StringUtils.isNotEmpty(createTime)){
                LocalDateTime localDate = LocalDateTime.parse(TimeStampUtils.timeStampToDateString(createTime),dtf);
                //获取创建时间和当前时间的差额
                long  days= ChronoUnit.DAYS.between(localDate, nowtime);
                //将创建时间格式化
                externalContact.setCreateTimeStr(TimeStampUtils.timeStampToDateString(createTime));
                if(days>0){
                    if("onday".equals(addtime)){
                        if(days<=1){
                            result.add(externalContact);
                        }
                    }else if("onweek".equals(addtime)){
                        if(days<=7){
                            result.add(externalContact);
                        }
                    }else if("onmonth".equals(addtime)){
                        if(days<=30){
                            result.add(externalContact);
                        }
                    }else if("other".equals(addtime)){
                        if(days>30){
                            result.add(externalContact);
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<ExternalContact> getQywxGuidanceList( String followUserId, String relation, String loss, String stagevalue, String interval,Integer offset, Integer limit) {
        List<ExternalContact> result =  externalContactMapper.getQywxGuidanceList(followUserId,relation,loss,stagevalue,interval,offset,limit);
        for (ExternalContact externalContact : result) {
            //设置添加时间
            externalContact.setCreateTimeStr(TimeStampUtils.timeStampToDateString(externalContact.getCreatetime()));
            //设置标签
        }
        return result;
    }

    @Override
    public int getQywxGuidanceCount(String followUserId, String relation, String loss, String stagevalue, String interval) {
        return externalContactMapper.getQywxGuidanceCount(followUserId, relation, loss, stagevalue, interval);
    }

    @Override
    public int getgetAddTimeCount(String followUserId) {
        return externalContactMapper.getgetAddTimeCount(followUserId);
    }

    @Override
    public ExternalContact selectExternalUser(String followUserId, String externalUserId) {
        return externalContactMapper.selectExternalUser(followUserId,externalUserId);
    }

}
