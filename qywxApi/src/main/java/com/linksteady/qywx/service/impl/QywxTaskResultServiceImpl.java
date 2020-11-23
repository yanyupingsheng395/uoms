package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.QywxTaskResultMapper;
import com.linksteady.qywx.domain.QywxMsgResult;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.service.QywxTaskResultService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class QywxTaskResultServiceImpl implements QywxTaskResultService {

    @Autowired
    QywxTaskResultMapper qywxTaskResultMapper;

    @Autowired
    QywxService qywxService;

    @Override
    public void syncPushResult() throws WxErrorException {
        //获取所有待同步的msgId
        List<String> msgIdList=qywxTaskResultMapper.getPushMsgIdList();

        for(String msgId:msgIdList)
        {
            //删除响应的结果
            qywxTaskResultMapper.deletePushResult(msgId);

            //重新同步结果
            JSONObject param=new JSONObject();
            param.put("msgid",msgId);

            StringBuffer requestUrl = new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_GROUP_MSG_RESULT));
            requestUrl.append(qywxService.getAccessToken());

            String result= OkHttpUtil.postRequestByJson(requestUrl.toString(),param.toJSONString());
            log.info("{}获取推送的结果为{}",msgId,result);

            JSONObject jsonObject = JSON.parseObject(result);
            String code = jsonObject.getString("code");

            if(StringUtils.isNotEmpty(code)&&code.equalsIgnoreCase("200"))
            {
                //构造结果
                JSONArray detailList = jsonObject.getJSONArray("data");

                List<QywxMsgResult> qywxMsgResultList= Lists.newArrayList();
                QywxMsgResult qywxMsgResult=null;
                JSONObject obj = null;
                for(int i=0;i<detailList.size();i++)
                {
                    obj=detailList.getJSONObject(i);
                    qywxMsgResult=new QywxMsgResult();

                    qywxMsgResult.setExternalUserId(obj.getString("external_userid"));
                    qywxMsgResult.setChatId(obj.getString("chat_id"));
                    qywxMsgResult.setFollowUserId(obj.getString("userid"));
                    qywxMsgResult.setStatus(obj.getIntValue("status"));
                    qywxMsgResult.setSendTime(obj.getString("send_time"));
                    if(obj.getIntValue("status")==1)
                    {
                        qywxMsgResult.setSendTimeDt(new Date(Long.parseLong(obj.getString("send_time"))*1000));
                    }
                    qywxMsgResult.setMsgId(msgId);
                    qywxMsgResultList.add(qywxMsgResult);
                }
                qywxTaskResultMapper.saveMsgResult(qywxMsgResultList);
            }else
            {
                log.error("通过微信推送结果失败，返回结果为{}",result);
            }
        }
    }

    @Override
    public void updateExecStatus() {
        qywxTaskResultMapper.updateExecStatus();
    }
}
