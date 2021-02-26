package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.qywx.dao.AddUserMapper;
import com.linksteady.qywx.dao.AddUserTriggerMapper;
import com.linksteady.qywx.dao.QywxParamMapper;
import com.linksteady.qywx.domain.*;
import com.linksteady.qywx.service.AddUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
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
public class AddUserServiceImpl implements AddUserService {

    @Autowired
    private AddUserMapper addUserMapper;

    @Autowired
    private AddUserTriggerMapper addUserTriggerMapper;

    @Autowired
    QywxParamMapper qywxParamMapper;


    @Override
    public int getHeadCount() {
        return addUserMapper.getHeadCount();
    }

    @Override
    public List<AddUserHead> getHeadPageList(int limit, int offset) {
        return addUserMapper.getHeadPageList(limit, offset);
    }

    /**
     * 对主记录进行保存
     * @param addUserHead
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddUserHead saveData(AddUserHead addUserHead,String opUser) throws Exception{
        addUserHead.setTaskStatus("edit");
        addUserHead.setInsertDt(new Date());
        addUserHead.setInsertBy(opUser);
        addUserHead.setUpdateDt(new Date());
        addUserHead.setUpdateBy(opUser);
        addUserMapper.saveHeadData(addUserHead);
        //更新参数
        filterUsers(addUserHead.getId(), addUserHead.getSourceId(), addUserHead.getRegionId());

        return addUserMapper.getHeadById(addUserHead.getId());
    }

    @Override
    public void deleteTask(String id) {
        addUserMapper.deleteHead(id);
    }

    @Override
    public List<Map<String, String>> getSource() {
        return addUserMapper.getSource();
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
        int count = addUserMapper.getAddUserListCount(headId);

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
        addUserMapper.insertAddUserList(headId, whereInfo.toString());

        //计算推送节奏参数
        //推送总人数(重新进行查询)
        count = addUserMapper.getAddUserListCount(headId);

        if(count==0)
        {
            throw new Exception("无符合条件的用户,请修改条件重新筛选!");
        }

        //获取默认的 每日推送人数 及 推送转化率
        QywxParam qywxParam = qywxParamMapper.getQywxParam();

        if(null==qywxParam||qywxParam.getActiveNum()==0)
        {
            throw new Exception("尚未为任务配置推送人数限制!");
        }
        int activeNum=qywxParam.getActiveNum();
        double applyRate=qywxParam.getDailyAddRate();


        int dailyAddNum = 0;
        int waitDays = 0;
        int addTotal = 0;
        //计算预计每日添加好友人数  预计全部推送所需天数  预计添加好友总人数
        if (count > 0) {
            dailyAddNum = (int) Math.floor(activeNum * applyRate/100);
            waitDays = count%activeNum==0?count / activeNum:(count / activeNum+1);
            addTotal = (int) Math.floor(applyRate * count/100);
        }
        //更新记录
        addUserMapper.updatePushParameter(headId, count, activeNum, applyRate, dailyAddNum, waitDays, addTotal);
    }

    /**
     * 执行一次推送任务
     *
     * @param headId
     */
    @Override
    @Transactional
    public void execTask(long headId,String opUserName) throws Exception {
        AddUserHead addUserHead=addUserMapper.getHeadById(headId);
        //更新版本号
        int count=addUserMapper.updateHeadVersion(headId,addUserHead.getVersion());

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
        if(addUserMapper.getRunningSchedule(Long.parseLong(currentDay))>0)
        {
            throw new Exception("当前日期已存在执行中的推送，为避免触发企业微信人数上限，请选择明日再进行推送!");
        }

        if(StringUtils.isEmpty(addUserHead.getSmsContent()))
        {
            throw new Exception("当前任务尚未配置文案!");
        }

        if(null==addUserHead.getContactWayId()||StringUtils.isEmpty(addUserHead.getContactWayUrl()))
        {
            throw new Exception("当前任务尚未配置渠道二维码!");
        }

        //删除推送历史表中超过N天的记录
        qywxParamMapper.deleteAddUserHistory(7);

        //获取今天预计推送人数
        long dailyUserCnt=addUserHead.getDailyUserCnt();

        //最终待推送的人数，如果剩余>预计，则为预计人数 否则为剩余人数
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
        addUserSchedule.setWaitAddNum((long)Math.floor(targetNum*addUserHead.getDailyApplyRate()/100));
        //本次推送后剩余人数
        long afterWaitNum=waitUserCnt-targetNum;
        addUserSchedule.setRemainUserCnt(afterWaitNum);
        //剩余人数预计推送天数
        addUserSchedule.setWaitDays(afterWaitNum/addUserHead.getDailyUserCnt()+1);
        //按当前转化率剩余人数预计添加好友数量
        addUserSchedule.setRemainAddNum((long)Math.floor(afterWaitNum*addUserHead.getDailyApplyRate()));

        QywxContactWay qywxContactWay=addUserTriggerMapper.getContactWayById(addUserHead.getContactWayId());
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
        addUserSchedule.setScheduleDateWid(Long.parseLong(currentDay));
        addUserSchedule.setApplyPassNum(0);
        //默认全部推送成功
        addUserSchedule.setApplySuccessNum(targetNum);

        addUserMapper.saveAddUserSchedule(addUserSchedule);

        long scheduleId=addUserSchedule.getScheduleId();

        //根据targetNum进行分页
        int pageSize=100;
        long pageNum=targetNum%pageSize==0?targetNum/pageSize:(targetNum/pageSize+1);

        List<AddUser> addUserList=null;
        List<AddUser> targetAddUserList=null;
        for(int j=0;j<pageNum;j++)
        {
            //获取本批次要处理的数据
            addUserList=addUserMapper.getUnProcessAddUserList(headId,pageSize);
            //更新这批ID的scheduleId
            addUserMapper.updateAddUserList(headId,scheduleId,addUserList,addUserSchedule.getState(),addUserSchedule.getSmsContent());
            //将这批明细放入到短信推送表中去
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
            long scheduleDate= Long.parseLong(LocalDateTime.now().plusHours(1).format(dateTimeFormatter));

            targetAddUserList=Lists.newArrayList();
            //获取历史的手机号
            Map<String,String> addHistory= qywxParamMapper.getAddUserHistory(7).stream().collect(Collectors.toMap(AddUserHistoryVO::getPhoneNum,AddUserHistoryVO::getPhoneNum));
            for(AddUser addUser:addUserList)
            {
                if(addHistory.containsKey(addUser.getPhoneNum()))
                {
                    log.debug("{}重复存在于拉新历史表中，忽略，",addUser.getPhoneNum());
                    continue;
                }
                //通过数据库方式判断重复，太慢
//                try {
//                    SpringUtils.getAopProxy(this).insertToHistory(addUser.getPhoneNum());
//                } catch (Exception e) {
//                    log.debug("{}写入历史表错误，原因:{}",addUser.getPhoneNum(),e);
//                    continue;
//                }
                targetAddUserList.add(addUser);
            }

            if(targetAddUserList.size()>0)
            {
                qywxParamMapper.insertAddUserListHistory(targetAddUserList.stream().map(i->i.getPhoneNum()).collect(Collectors.toList()));
                addUserMapper.pushToPushListLarge(targetAddUserList,scheduleDate);
            }
        }

        //更新主记录表的剩余人数、更新任务状态为执行中,实际发送人数
        addUserMapper.updateHeadWaitUserCnt(headId,afterWaitNum,finishNum,opUserName);
    }

    @Override
    public AddUserHead saveDailyUserData(String headId, String dailyUserCnt, String dailyApplyRate) {
        //界面设置的每日推送人数
        Integer dailyUserCntLong = Integer.parseInt(dailyUserCnt);
        //界面设置的转化率
        Double dailyApplyRateDouble = Double.parseDouble(dailyApplyRate);
        //获取当前任务剩余的推送人数
        AddUserHead addUserHead = addUserMapper.getHeadById(Long.parseLong(headId));
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
        addUserMapper.updatePushParameter(Long.parseLong(headId), count, dailyUserCntLong, dailyApplyRateDouble, dailyAddNum, waitDays, addTotal);
        return addUserMapper.getHeadById(Long.parseLong(headId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSmsContentAndContactWay(String headId, String smsContent, String contactWayId, String contactWayUrl) {
        addUserMapper.updateSmsContentAndContactWay(headId, smsContent, contactWayId, contactWayUrl);
    }


    @Override
    public Map<String, Object> getTaskResultData(String headId) {
        Map<String, Object> result = Maps.newHashMap();
        // 获取发送范围
        AddUserHead addUserHead = addUserMapper.getHeadById(Long.parseLong(headId));
        String sendType = "0".equalsIgnoreCase(addUserHead.getSendType()) ? "全部用户" : addUserHead.getSourceName() + "|" + addUserHead.getRegionName();
        // 发送申请数+申请通过数
        List<Map<String, Object>> sendAndApplyData = addUserMapper.getSendAndApplyData(headId);
        // 统计所有数据
        List<Map<String, Object>> statisApplyData = Lists.newArrayList();
        if (sendAndApplyData.size() > 0) {
            // 申请通过随统计日期变化图
            String scheduleId = sendAndApplyData.get(0).get("scheduleid").toString();
            statisApplyData = addUserMapper.getStatisApplyData(headId, scheduleId);

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
        return addUserMapper.getStatisApplyData(headId, scheduleId);
    }

    @Override
    public int getScheduleCount(long headId) {
        return addUserMapper.getScheduleCount(headId);
    }

    @Override
    public Map<Long, Object> geRegionData() {
        Map<Long, Object> result = Maps.newHashMap();
        List<Map<Long, Object>> test = addUserMapper.geRegionData();
        Map<Long, List<Map<Long, Object>>> pid = test.stream().collect(Collectors.groupingBy(x -> Long.parseLong(x.get("pid").toString())));
        pid.entrySet().stream().forEach(x -> {
            result.put(x.getKey(), x.getValue().stream().collect(Collectors.toMap(k -> k.get("id"), v -> v.get("name"))));
        });
        return result;
    }

    @Override
    public AddUserHead getHeadById(long id) {
        return addUserMapper.getHeadById(id);
    }

    /**
     * 将手机号放入推送历史表中 使用新的事务
     * @param phoneNum
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRES_NEW)
    public void insertToHistory(String phoneNum) throws Exception
    {
        qywxParamMapper.insertAddUserHistory(phoneNum);
    }
}
