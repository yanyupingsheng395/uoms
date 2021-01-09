package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.bo.UserBo;
import com.linksteady.operate.dao.*;
import com.linksteady.operate.domain.*;
import com.linksteady.operate.domain.enums.ActivityPlanTypeEnum;
import com.linksteady.operate.domain.enums.ActivityStatusEnum;
import com.linksteady.operate.service.QywxActivityHeadService;
import com.linksteady.operate.vo.FollowUserVO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hxcao
 * @date 2019-08-13
 */
@Service
public class QywxActivityHeadServiceImpl implements QywxActivityHeadService {

    @Autowired
    private QywxActivityHeadMapper qywxActivityHeadMapper;

    @Autowired
    private QywxActivityUserGroupMapper qywxActivityUserGroupMapper;

    @Autowired
    private QywxActivityProductMapper qywxActivityProductMapper;

    @Autowired
    private QywxActivityPlanMapper qywxActivityPlanMapper;

    @Autowired
    private QywxActivityDetailMapper qywxActivityDetailMapper;

    @Override
    public List<ActivityHead> getDataListOfPage(int limit, int offset, String name, String date, String status) {
        return qywxActivityHeadMapper.getDataListOfPage(limit,offset, name, date, status);
    }

    @Override
    public int getDataCount(String name, String date, String status) {
        return qywxActivityHeadMapper.getDataCount(name,date,status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveActivityHead(ActivityHead activityHead, String coupons) {
        activityHead.setFormalStatus(ActivityStatusEnum.EDIT.getStatusCode());
        activityHead.setFormalNotifyStatus(ActivityStatusEnum.EDIT.getStatusCode());
        activityHead.setInsertDt(new Date());
        activityHead.setInsertBy(((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername());
        qywxActivityHeadMapper.saveActivityHead(activityHead);
        saveGroupInfo(activityHead.getHeadId());
        List<ActivityCoupon> couponList = JSONArray.parseArray(coupons, ActivityCoupon.class);
        if(couponList.size() > 0) {
            couponList.forEach(x->x.setHeadId(activityHead.getHeadId()));
            qywxActivityHeadMapper.saveActivityCouponList(couponList);
        }
        return activityHead.getHeadId().intValue();
    }

    @Override
    public ActivityHead findById(Long headId) {
        return qywxActivityHeadMapper.findById(headId);
    }

    @Override
    public String getActivityName(String headId) {
        return qywxActivityHeadMapper.getActivityName(headId);
    }

    @Override
    public int getActivityStatus(String id) {
        return qywxActivityHeadMapper.getActivityStatus(id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteData(Long headId) {
        qywxActivityHeadMapper.deleteActivity(headId);
        qywxActivityProductMapper.deleteData(headId);
        qywxActivityPlanMapper.deletePlan(headId);
        qywxActivityUserGroupMapper.deleteData(headId);
        //删除活动明细数据
        qywxActivityDetailMapper.deleteData(headId);
    }

    /**
     * 判断当前活动是否可以删除
     * 可以删除的条件： 要么全部为待计划状态，要么全部为过期未执行状态
     * @param headId
     * @return
     */
    @Override
    public int getDeleteCount(Long headId) {
        return qywxActivityHeadMapper.getDeleteCount(headId);
    }

    /**
     *
     * @param headId
     * @param status
     * @param type   计划类型 通知 or 正式
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long headId, String status, String type) {
        if(ActivityPlanTypeEnum.Notify.getPlanTypeCode().equalsIgnoreCase(type)) {
            qywxActivityHeadMapper.updateFormalStatusHead(headId,status,ActivityPlanTypeEnum.Notify.getPlanTypeCode());
        } else if(ActivityPlanTypeEnum.During.getPlanTypeCode().equalsIgnoreCase(type)) {
            qywxActivityHeadMapper.updateFormalStatusHead(headId,status,ActivityPlanTypeEnum.During.getPlanTypeCode());
        }
    }

    @Override
    public void expireActivityHead() {
        qywxActivityHeadMapper.expireFormalNotify();
        qywxActivityHeadMapper.expireFormalDuring();
    }

    @Override
    public List<ActivityCoupon> findCouponList(Long headId) {
        return qywxActivityHeadMapper.getActivityCouponList(headId);
    }

    @Override
    public List<FollowUserVO> getAllFollowUserList(Long headId) {
        return qywxActivityHeadMapper.getAllFollowUserList(headId);
    }

    @Override
    public ActivityHead getActivityHeadById(Long headId) {
        return qywxActivityHeadMapper.getActivityHeadById(headId);
    }

    @Override
    public Map<String, Object> getPushEffectChange(Long headId) {
        Map<String, Object> result = Maps.newHashMap();
        String dateFormat = "yyyyMMdd";
        List<LocalDate> xdatas = Lists.newLinkedList();

        ActivityHead qywxDailyHeader = qywxActivityHeadMapper.getActivityHeadById(headId);
        // 提交任务日期
        // TODO: 2021/1/9 这个地方需要修改，先讨论
        String taskDt ="20201202";

        //获取任务观察的天数
        long effectDays = qywxDailyHeader.getEffectDays();

        //任务提交的日期
        LocalDate taskDtDate = LocalDate.parse(taskDt, DateTimeFormatter.ofPattern(dateFormat));

        // 任务期最后时间
        LocalDate maxDate = taskDtDate.plusDays(effectDays + 1);

        //时间轴的数据
        while (taskDtDate.isBefore(maxDate)) {
            xdatas.add(taskDtDate);
            taskDtDate = taskDtDate.plusDays(1);
        }

        List<QywxActivityStatis> dataList = qywxActivityHeadMapper.getQywxActivityStatisList(headId);
        Map<String, QywxActivityStatis> dailyStatisMap = dataList.stream().collect(Collectors.toMap(QywxActivityStatis::getConversionDateStr, a -> a));

        //转化人数
        List<Long> convertNumList = Lists.newArrayList();
        List<Double> convertRateList = Lists.newArrayList();
        List<Long> convertSpuNumList = Lists.newArrayList();
        List<Double> convertSpuRateList = Lists.newArrayList();

        xdatas.forEach(x -> {
            //判断当前是否有数据
            QywxActivityStatis qywxDailyStatis = dailyStatisMap.get(x.format(DateTimeFormatter.ofPattern(dateFormat)));

            //找不到转化数据
            if (null == qywxDailyStatis) {
                if (x.isAfter(LocalDate.now()) || x.isEqual(LocalDate.now())) {
                    //填充空值
                    convertNumList.add(null);
                    convertRateList.add(null);
                    convertSpuNumList.add(null);
                    convertSpuRateList.add(null);
                } else {
                    //填充0
                    convertNumList.add(0L);
                    convertRateList.add(0D);
                    convertSpuNumList.add(0L);
                    convertSpuRateList.add(0D);
                }
            } else {
                convertNumList.add(qywxDailyStatis.getConvertNum());
                convertRateList.add(qywxDailyStatis.getConvertRate());
                convertSpuNumList.add(qywxDailyStatis.getConvertSpuNum());
                convertSpuRateList.add(qywxDailyStatis.getConvertSpuRate());
            }
        });

        result.put("xdata", xdatas.stream().map(x -> x.format(DateTimeFormatter.ofPattern(dateFormat))).collect(Collectors.toList()));
        result.put("ydata1", convertNumList);
        result.put("ydata2", convertRateList);
        result.put("ydata3", convertSpuNumList);
        result.put("ydata4", convertSpuRateList);
        return result;
    }

    @Override
    public List<QywxActivityConvertDetail> getConvertDetailData(int limit, int offset, Long headId) {
        return qywxActivityHeadMapper.getConvertDetailData(limit,offset,headId);
    }


    @Override
    public int getConvertDetailCount(Long headId) {
        return qywxActivityHeadMapper.getConvertDetailCount(headId);
    }

    @Override
    public QywxActivityStaffEffect getActivityStaffEffect(Long headId, String followUserId) {
        return qywxActivityHeadMapper.getActivityStaffEffect(headId,followUserId) ;
    }

    /**
     * 保存群组信息
     * @param headId
     */
    private void saveGroupInfo(long headId) {
        //类型 NOTIFY 通知 DURING 期间
        List<ActivityGroup> activityGroups = Lists.newArrayList();
        activityGroups.add(new ActivityGroup(
                9L,headId, "NOTIFY", "满件打折" , ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户对该策略下的商品兴趣较高，且在本次活动中发生购买的概率较大"
        ));
        activityGroups.add(new ActivityGroup(
                13L, headId, "NOTIFY", "仅店铺券", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户当前所处的价值、活跃度、年内购买次数维度，在历史活动中转化率较高,且在该商品上转化较高"
        ));
        activityGroups.add(new ActivityGroup(
                14L, headId,"NOTIFY", "立减特价", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户对该策略下的商品兴趣较高，且在本次活动中发生购买的概率较大"
        ));
        activityGroups.add(new ActivityGroup(
                9L,headId, "DURING", "满件打折" , ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户对该策略下的商品兴趣较高，且在本次活动中发生购买的概率较大"
        ));
        activityGroups.add(new ActivityGroup(
                13L, headId, "DURING", "仅店铺券", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户当前所处的价值、活跃度、年内购买次数维度，在历史活动中转化率较高,且在该商品上转化较高"
        ));
        activityGroups.add(new ActivityGroup(
                14L, headId,"DURING", "立减特价", ((UserBo)SecurityUtils.getSubject().getPrincipal()).getUsername(), new Date(), "用户对该策略下的商品兴趣较高，且在本次活动中发生购买的概率较大"
        ));
        qywxActivityUserGroupMapper.saveGroupData(activityGroups);
    }
}