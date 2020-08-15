package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.AddUserTriggerMapper;
import com.linksteady.operate.dao.QywxContactWayMapper;
import com.linksteady.operate.dao.QywxParamMapper;
import com.linksteady.operate.domain.AddUserHead;
import com.linksteady.operate.domain.AddUserSchedule;
import com.linksteady.operate.domain.QywxContactWay;
import com.linksteady.operate.domain.QywxParam;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.AddUserTriggerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2020/7/16
 */
@Service
@Slf4j
public class AddUserTriggerServiceImpl implements AddUserTriggerService {

    @Autowired
    private AddUserTriggerMapper addUserTriggerMapper;

    @Autowired
    private QywxParamMapper qywxParamMapper;

    @Autowired
    private QywxContactWayMapper qywxContactWayMapper;

    @Override
    public int getHeadCount() {
        return addUserTriggerMapper.getHeadCount();
    }

    @Override
    public List<AddUserHead> getHeadPageList(int limit, int offset) {
        return addUserTriggerMapper.getHeadPageList(limit, offset);
    }

    /**
     * 对主记录进行保存
     * @param addUserHead
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveData(AddUserHead addUserHead) {
        addUserHead.setTaskStatus("edit");
        addUserTriggerMapper.saveHeadData(addUserHead);
    }

    @Override
    public void deleteTask(String id) {
        addUserTriggerMapper.deleteHead(id);
    }

    @Override
    public List<Map<String, String>> getSource() {
        return addUserTriggerMapper.getSource();
    }


    /**
     * 执行一次推送任务
     *
     * @param headId
     */
    @Override
    @Transactional
    public void execTask(long headId,String opUserName) throws Exception {
        AddUserHead addUserHead=addUserTriggerMapper.getHeadById(headId);
        //更新版本号
        int count=addUserTriggerMapper.updateHeadVersion(headId,addUserHead.getVersion());

        if(count==0)
        {
            throw new OptimisticLockException("记录已被其他用户修改，请返回列表界面重试！");
        }

        String status=addUserHead.getTaskStatus();
        //判断任务的状态
        if("doing".equals(status))
        {
            throw new Exception("当前任务已在执行中!");
        }

        //判断当天是否已经有推送计划
        if(addUserTriggerMapper.getRunningScheduleCount()>0)
        {
            throw new Exception("当前已存在执行中的推送，为避免触发企业微信人数上限，请选择明日再进行推送!");
        }

        QywxParam qywxParam=qywxParamMapper.getQywxParam();

        if(qywxParam.getTriggerNum()==0)
        {
            throw new Exception("当前推送任务分配的推送人数为0!");
        }

        if(StringUtils.isEmpty(addUserHead.getSmsContent()))
        {
            throw new Exception("当前任务尚未配置文案!");
        }

        if(StringUtils.isEmpty(addUserHead.getContactWayUrl())||null==addUserHead.getContactWayId())
        {
            throw new Exception("当前任务尚未配置渠道活码!");
        }

        //写入推送计划
        AddUserSchedule addUserSchedule=new AddUserSchedule();
        addUserSchedule.setHeadId(addUserHead.getId());
        //主动触发预计的人数
        addUserSchedule.setApplyNum(qywxParam.getTriggerNum());

        //预计的推送转化率
        addUserSchedule.setApplyRate(qywxParam.getDailyAddRate());
        //预计本次添加用户人数
        addUserSchedule.setWaitAddNum((long)Math.floor(qywxParam.getTriggerNum()*qywxParam.getDailyAddRate()));

        addUserSchedule.setRemainUserCnt(qywxParam.getTriggerNum());

        QywxContactWay qywxContactWay=qywxContactWayMapper.getContactWayById(addUserHead.getContactWayId());
        addUserSchedule.setContactwayId(qywxContactWay.getContactWayId());
        addUserSchedule.setState(qywxContactWay.getState());
        addUserSchedule.setContactwayUrl(addUserHead.getContactWayUrl());

        addUserSchedule.setSmsContent(addUserHead.getSmsContent());
        addUserSchedule.setStatus("doing");
        addUserSchedule.setInsertBy(opUserName);
        addUserSchedule.setInsertDt(new Date());
        addUserSchedule.setUpdateBy(opUserName);
        addUserSchedule.setUpdateDt(new Date());
        addUserSchedule.setScheduleDate(new Date());
        String currentDay=DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        addUserSchedule.setScheduleDateWid(Long.parseLong(currentDay));
        addUserSchedule.setApplyPassNum(0);
        addUserSchedule.setApplySuccessNum(0);
        addUserSchedule.setActualApplyNum(0);

        addUserTriggerMapper.saveAddUserSchedule(addUserSchedule);

        //更新更新任务状态为执行中
        addUserTriggerMapper.updateHeadToDoing(headId,opUserName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSmsContentAndContactWay(String headId, String smsContent, String contactWayId, String contactWayUrl, String isSourceName, String isProdName) {
        addUserTriggerMapper.updateSmsContentAndContactWay(headId, smsContent, contactWayId, contactWayUrl, isSourceName, isProdName);
    }

    @Override
    public Map<String, Object> getTaskResultData(String headId) {
        Map<String, Object> result = Maps.newHashMap();
        // 获取发送范围
        AddUserHead addUserHead = addUserTriggerMapper.getHeadById(Long.parseLong(headId));
        String sendType = "0".equalsIgnoreCase(addUserHead.getSendType()) ? "全部用户" : addUserHead.getSourceName() + "|" + addUserHead.getRegionName();
        // 发送申请数+申请通过数
        List<Map<String, Object>> sendAndApplyData = addUserTriggerMapper.getSendAndApplyData(headId);
        // 统计所有数据
        List<Map<String, Object>> statisApplyData = Lists.newArrayList();
        if (sendAndApplyData.size() > 0) {
            // 申请通过随统计日期变化图
            String scheduleId = sendAndApplyData.get(0).get("scheduleid").toString();
            statisApplyData = addUserTriggerMapper.getStatisApplyData(headId, scheduleId);
        }
        result.put("statisApplyData", JSON.toJSONString(statisApplyData));
        result.put("applyUserCnt", addUserHead.getApplyUserCnt() == null ? "-" : addUserHead.getApplyUserCnt());
        result.put("applyPassCnt", addUserHead.getApplyPassCnt() == null ? "-" : addUserHead.getApplyPassCnt());
        result.put("applyPassRate", addUserHead.getApplyPassRate() == null ? "-" : addUserHead.getApplyPassRate());
        result.put("sendAndApplyData", JSON.toJSONString(sendAndApplyData));
        result.put("sendType", sendType);
        return result;
    }

    @Override
    public List<Map<String, Object>> getStatisApplyData(String headId, String scheduleId) {
        return addUserTriggerMapper.getStatisApplyData(headId, scheduleId);
    }

    @Override
    public int getScheduleCount(long headId) {
        return addUserTriggerMapper.getScheduleCount(headId);
    }

    @Override
    public Map<Long, Object> geRegionData() {
        Map<Long, Object> result = Maps.newHashMap();
        List<Map<Long, Object>> test = addUserTriggerMapper.geRegionData();
        Map<Long, List<Map<Long, Object>>> pid = test.stream().collect(Collectors.groupingBy(x -> Long.parseLong(x.get("pid").toString())));
        pid.entrySet().stream().forEach(x -> {
            result.put(x.getKey(), x.getValue().stream().collect(Collectors.toMap(k -> k.get("id"), v -> v.get("name"))));
        });
        return result;
    }

    @Override
    public String getStatus(String headId) {
        return addUserTriggerMapper.getStatus(headId);
    }

    @Override
    public List<String> getStatusList() {
        return addUserTriggerMapper.getStatusList();
    }

    @Override
    public AddUserHead getHeadById(long id) {
        return addUserTriggerMapper.getHeadById(id);
    }

}
