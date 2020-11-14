package com.linksteady.operate.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.operate.domain.QywxMsgResult;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.service.QywxDailyService;
import com.linksteady.operate.task.common.CommonExecutors;
import com.linksteady.operate.task.common.ExecType;
import com.linksteady.smp.starter.annotation.JobHandler;
import com.linksteady.smp.starter.domain.ResultInfo;
import com.linksteady.smp.starter.jobclient.service.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * 对推送效果进行计算
 * @author huang
 */
@Slf4j(topic = "jobLog")
@Component
@JobHandler(value = "qywxDailyTask")
public class qywxDailyTask extends IJobHandler {

   @Autowired
   QywxDailyService qywxDailyService;

   @Autowired
    ConfigService configService;

    @Override
    public ResultInfo execute(String param) {
        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        log.info("开始企业微信任务的计算，开始的时间为:{}", dtf2.format(LocalDateTime.now()));

        try {
            //获取所有待同步的msgId
            List<String> msgIdList=qywxDailyService.getPushMsgIdList();

            for(String msgId:msgIdList) {
                //删除响应的结果
                qywxDailyService.deletePushResult(msgId);
                //重新同步结果
                JSONObject josnParam = new JSONObject();
                josnParam.put("msgid", msgId);

                String corpId = configService.getValueByName(ConfigEnum.qywxCorpId.getKeyCode());
                String timestamp = String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
                String signature = SHA1.gen(timestamp, josnParam.toJSONString());
                String qywcDomainUrl = configService.getValueByName(ConfigEnum.qywxDomainUrl.getKeyCode());
                String url = qywcDomainUrl + "/push/getGroupMsgResult?corpId=" + corpId + "&timestamp=" + timestamp + "&signature=" + signature;

                String result = OkHttpUtil.postRequestByJson(url, josnParam.toJSONString());
                log.info("{}获取推送的结果为{}", msgId, result);

                JSONObject jsonObject = JSON.parseObject(result);
                String code = jsonObject.getString("code");

                if (StringUtils.isNotEmpty(code) && code.equalsIgnoreCase("200")) {
                    //构造结果
                    JSONArray detailList = jsonObject.getJSONArray("data");

                    List<QywxMsgResult> qywxMsgResultList = Lists.newArrayList();
                    QywxMsgResult qywxMsgResult = null;
                    JSONObject obj = null;
                    for (int i = 0; i < detailList.size(); i++) {
                        obj = detailList.getJSONObject(i);
                        qywxMsgResult = new QywxMsgResult();

                        qywxMsgResult.setExternalUserId(obj.getString("external_userid"));
                        qywxMsgResult.setChatId(obj.getString("chat_id"));
                        qywxMsgResult.setFollowUserId(obj.getString("userid"));
                        qywxMsgResult.setStatus(obj.getIntValue("status"));
                        qywxMsgResult.setSendTime(obj.getString("send_time"));
                        if (obj.getIntValue("status") == 1) {
                            qywxMsgResult.setSendTimeDt(new Date(Long.parseLong(obj.getString("send_time")) * 1000));
                        }
                        qywxMsgResult.setMsgId(msgId);

                        qywxMsgResultList.add(qywxMsgResult);
                    }
                    qywxDailyService.saveMsgResult(qywxMsgResultList);

                } else {
                    log.error("通过微信推送结果失败，返回结果为{}", result);
                }
            }

            //更新明细表的执行状态
            qywxDailyService.updateExecStatus();

            log.info("完成企业微信任务的计算，完成的时间为:{}", dtf2.format(LocalDateTime.now()));
            return ResultInfo.success("执行任务成功!");

        } catch (Exception e) {
            log.error("企业微信任务计算失败，失败的原因:{}",e);
            return ResultInfo.faild(e.getMessage());
        }
    }

}


