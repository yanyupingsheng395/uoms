package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.AddUserTriggerMapper;
import com.linksteady.operate.domain.AddUserHead;
import com.linksteady.operate.domain.AddUserSchedule;
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
        try {
            filterUsers(addUserHead.getId(), addUserHead.getSourceId(), addUserHead.getRegionId());
        } catch (Exception e) {
            log.error("计算数据出错", e);
        }
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
     * 根据条件找出符合的用户
     *
     * @param headId
     * @param sourceId
     * @param regionIds
     */
    @Override
    @Transactional
    public void filterUsers(long headId, String sourceId, String regionIds) throws Exception {
        //判断当前head_id下是否已存在明细数据 如果是，抛出异常
        int count = addUserTriggerMapper.getAddUserListCount(headId);

        if (count > 0) {
            throw new Exception("当前记录已存在推送明细,请不要重复操作!");
        }
        //构造查询条件
        StringBuffer whereInfo = new StringBuffer();

        if (StringUtils.isNotEmpty(sourceId)) {
            whereInfo.append(" and  position('" + sourceId + "' in source) > 0 ");
        }
        //构造regions查询条件
        if (StringUtils.isNotEmpty(regionIds)) {
            StringBuffer subWhere = new StringBuffer(" and (");
            List<String> regionList = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(regionIds);

            for (int i = 0; i < regionList.size(); i++) {
                if (i == 0) {
                    subWhere.append(" position('" + regionList.get(i) + "' in area) > 0 ");
                } else {
                    subWhere.append(" or position('" + regionList.get(i) + "' in area) > 0 ");
                }
            }
            subWhere.append(" )");

            whereInfo.append(subWhere);
        }

        //对筛选出的明细数据进行写入操作
        addUserTriggerMapper.insertAddUserList(headId, whereInfo.toString());

        //计算推送节奏参数
        //推送总人数(重新进行查询)
        count = addUserTriggerMapper.getAddUserListCount(headId);

        //获取默认的 每日推送人数 及 推送转化率
        int defaultAddcount;
        double defaultApplyRate;
        QywxParam qywxParam = addUserTriggerMapper.getQywxParam();
        if (null == qywxParam) {
            defaultAddcount = 2000;
            defaultApplyRate = 5;
        } else {
            defaultAddcount = qywxParam.getDailyAddNum();
            defaultApplyRate = qywxParam.getDailyAddRate();
        }


        int dailyAddNum = 0;
        int waitDays = 0;
        int addTotal = 0;
        //计算预计每日添加好友人数  预计全部推送所需天数  预计添加好友总人数
        if (count > 0) {
            dailyAddNum = (int) Math.floor(defaultAddcount * defaultApplyRate/100);
            waitDays = count%defaultAddcount==0?count / defaultAddcount:count / defaultAddcount+1;
            addTotal = (int) Math.floor(defaultApplyRate * count/100);
        }
        //更新记录
//        addUserTriggerMapper.updatePushParameter(headId, count, defaultAddcount, defaultApplyRate, dailyAddNum, waitDays, addTotal);
        addUserTriggerMapper.updatePushParameter(headId, defaultAddcount, defaultApplyRate, dailyAddNum, waitDays, addTotal);
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
        if("abort".equals(status))
        {
            throw new Exception("当前任务已终止，无法再执行!");
        }

        //判断任务的剩余人数
        long waitUserCnt=addUserHead.getWaitUserCnt();
        if(waitUserCnt==0)
        {
            throw new Exception("当前任务已无用户需要推送!");
        }

        //判断当天是否已经有推送计划
        String currentDay=DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        if(addUserTriggerMapper.getRunningSchedule(Long.parseLong(currentDay))>0)
        {
            throw new Exception("当前日期已存在执行中的推送，为避免触发企业微信人数上限，请选择明日再进行推送!");
        }

        //获取计划推送人数
        long dailyUserCnt=addUserHead.getDailyUserCnt();

        //最终待推送的人数，如果剩余>计划，则为计划人数 否则为剩余人数
        long targetNum=waitUserCnt>dailyUserCnt?dailyUserCnt:waitUserCnt;
        //本次推送完后 完成推送的人数
        long finishNum=addUserHead.getDeliveredUserCnt()+targetNum;

        //写入推送计划
        AddUserSchedule addUserSchedule=new AddUserSchedule();
        addUserSchedule.setHeadId(addUserHead.getId());
        addUserSchedule.setApplyNum(targetNum);

        //预计的推送转化率
        addUserSchedule.setApplyRate(addUserHead.getDailyApplyRate());
        //预计本次添加用户人数
        addUserSchedule.setWaitAddNum((long)Math.floor(targetNum*addUserHead.getDailyApplyRate()));
        //本次推送后剩余人数
        long afterWaitNum=waitUserCnt-targetNum;
        addUserSchedule.setRemainUserCnt(afterWaitNum);
        //剩余人数预计推送天数
        addUserSchedule.setWaitDays(afterWaitNum%addUserHead.getDailyUserCnt()+1);
        //按当前转化率剩余人数预计添加好友数量
        addUserSchedule.setRemainAddNum((long)Math.floor(afterWaitNum*addUserHead.getDailyApplyRate()));
        addUserSchedule.setContactwayId(addUserHead.getContactWayId());
        //todo
        addUserSchedule.setState("");
        addUserSchedule.setContactwayUrl(addUserHead.getContactWayUrl());

        addUserSchedule.setSmsContent(addUserHead.getSmsContent());
        addUserSchedule.setStatus("doing");
        addUserSchedule.setInsertBy(opUserName);
        addUserSchedule.setInsertDt(new Date());
        addUserSchedule.setUpdateBy(opUserName);
        addUserSchedule.setUpdateDt(new Date());
        addUserSchedule.setScheduleDate(new Date());
        addUserSchedule.setScheduleDateWid(Long.parseLong(currentDay));
        addUserSchedule.setApplyPassNum(0);

        addUserTriggerMapper.saveAddUserSchedule(addUserSchedule);

        long scheduleId=addUserSchedule.getScheduleId();

        //更新推送明细
        long updateNum=addUserTriggerMapper.updateAddUserList(headId,scheduleId,targetNum);

        if(updateNum!=targetNum)
        {
           throw new Exception("推送人员数量不足，请检查！");
        }

        //更新主记录表的剩余人数、更新任务状态为执行中
        addUserTriggerMapper.updateHeadWaitUserCnt(headId,afterWaitNum,finishNum,opUserName);

        //将推送明细数据放到短信表里面去
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        long scheduleDate= Long.parseLong(LocalDateTime.now().plusHours(1).format(dateTimeFormatter));
        addUserTriggerMapper.pushToPushListLarge(headId,scheduleId,scheduleDate);
    }

    @Override
    public AddUserHead saveDailyUserData(String headId, String dailyUserCnt, String dailyApplyRate) {
        //界面设置的每日推送人数
        Integer dailyUserCntLong = Integer.parseInt(dailyUserCnt);
        //界面设置的转化率
        Double dailyApplyRateDouble = Double.parseDouble(dailyApplyRate);
        //获取当前任务剩余的推送人数
        AddUserHead addUserHead = addUserTriggerMapper.getHeadById(Long.parseLong(headId));
        int count=(int)addUserHead.getWaitUserCnt();
        //每日添加用户人数
        int dailyAddNum = 0;
        //完成推送需要的天数
        int waitDays = 0;
        //总添加人数
        int addTotal = 0;
        //计算预计每日添加好友人数  预计全部推送所需天数  预计添加好友总人数
        if (count > 0) {
            dailyAddNum = (int) Math.floor(dailyUserCntLong * dailyApplyRateDouble/100);
            waitDays = count%dailyUserCntLong==0?count / dailyUserCntLong:count / dailyUserCntLong+1;
            addTotal = (int) Math.floor(dailyApplyRateDouble * count/100);
        }
        //更新记录
//        addUserTriggerMapper.updatePushParameter(Long.parseLong(headId), count, dailyUserCntLong, dailyApplyRateDouble, dailyAddNum, waitDays, addTotal);
        addUserTriggerMapper.updatePushParameter(Long.parseLong(headId), dailyUserCntLong, dailyApplyRateDouble, dailyAddNum, waitDays, addTotal);
        return addUserTriggerMapper.getHeadById(Long.parseLong(headId));
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
    public AddUserHead getHeadById(long id) {
        return addUserTriggerMapper.getHeadById(id);
    }

    /**
     * 自动跟新任务的状态、计划的状态 （为调度任务写的方法)
     */
    @Override
    public void autoUpdateStatus() {
        //更新最近三天的推送结果
        addUserTriggerMapper.updatePushResult();
        //对于 执行中的计划 判断其下面所有的用户都完成了推送，如果是，则更新其状态为 完成
        addUserTriggerMapper.updateScheduleToDone();
        //更新头表的状态(存在执行中的计划，则为执行中 否则为停止  如果所有的人都推送完了，则为已结束 )
        addUserTriggerMapper.updateHeadToStop();
        addUserTriggerMapper.updateHeadToDone();
        //更新企业微信拉新状态 todo

    }


    /**
     * 计算任务的效果
     */
    @Override
    public void calculateAddUserEffect() {
        //更新头表上的效果字段

        //更新schedule表上的效果字段

        //写逐日的转化统计表
    }

}
