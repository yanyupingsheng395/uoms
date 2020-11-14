package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
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

import java.util.List;

@Service
@Slf4j
public class ExternalContactServiceImpl implements ExternalContactService {

    @Autowired
    QywxService qywxService;

    @Autowired
    FollowUserService followUserService;

    @Autowired
    ExternalContactMapper externalContactMapper;

    /**
     * 同步外部联系人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncExternalContact() throws Exception
    {
        //设置当前企业所有外部客户的删除标记为1
        externalContactMapper.updateDeleteFlag();

        //获取配置了客户联系功能的成员列表
        List<String> followerUserList = followUserService.selectFollowUserList();
        for (String followerUserId : followerUserList) {
            //获取此成员下所有的外部客户信息
            List<String> externalUserList = findExternalContractList(followerUserId);

            //保存所有的外部客户信息
            externalContactMapper.saveExternalUserList(followerUserId,externalUserList);

            //使用批量接口

//            for (String externalContractUserId : externalUserList) {
//                //查询客户详细信息
//                ExternalContact externalContact = externalContactService.selectExternalContractDetail( corpId, followerUserId,externalContractUserId);
//                externalContactService.updateExternalContract(externalContact);
//            }
        }

        //删除
        externalContactMapper.deleteExternalUser();
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

    @Override
    public ExternalContact selectExternalContractDetail(String followerUserId, String externalUserid) throws WxErrorException {
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
        ExternalContact externalContact = new ExternalContact().buildFromJsonObject(jsonObject, followerUserId);
        return externalContact;
    }

//    /**
//     * 查询本地某个导购下的外部客户列表
//     *
//     * @param userId
//     * @return
//     */
//    @Override
//    public List<ExternalContact> selectLocalContractListByUserId(String userId) {
//        List<ExternalContact> result =externalContactMapper.selectLocalContractByUserId(userId);
//        for (ExternalContact externalContact : result) {
//            //设置添加时间
//            externalContact.setCreateTimeStr(TimeStampUtils.timeStampToDateString(externalContact.getCreatetime()));
//            //设置标签
//        }
//        return result;
//    }
//
//    @Override
//    public int selectLocalContracCountByUserId(String userId) {
//        return externalContactMapper.selectLocalContracCountByUserId(userId);
//    }


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

    @Override
    public void updateDeleteFlag() {
        externalContactMapper.updateDeleteFlag();
    }

    @Override
    public void deleteExternalUser() {
        externalContactMapper.deleteExternalUser();
    }
}
