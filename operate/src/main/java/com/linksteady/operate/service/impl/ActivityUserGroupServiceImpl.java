package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.dao.ActivityUserGroupMapper;
import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.service.ActivityUserGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author hxcao
 * @date 2019-11-02
 */
@Service
public class ActivityUserGroupServiceImpl implements ActivityUserGroupService {

    @Autowired
    private ActivityUserGroupMapper activityUserGroupMapper;

    @Override
    public List<ActivityGroup> getUserGroupPage(String headId, String stage, int start, int end) {
        return activityUserGroupMapper.getUserGroupPage(headId, stage, start, end);
    }

    @Override
    public List<ActivityGroup> getUserGroupList(String headId, String stage) {
        return activityUserGroupMapper.getUserGroupList(headId, stage);
    }

    @Override
    public int getCount(String headId, String stage) {
        return activityUserGroupMapper.getCount(headId, stage);
    }

    @Override
    public void updateGroupTemplate(String groupId, String code) {
        activityUserGroupMapper.updateGroupTemplate(groupId, code);
    }

    @Override
    public List<ActivityGroup> getActivityUserList(String headId, String stage) {
        List<ActivityGroup> userGroupList = activityUserGroupMapper.getUserGroupList(headId, stage);
        userGroupList.stream().filter(x-> StringUtils.isNotEmpty(x.getSmsTemplateContent())).forEach(x->{
            // 在你看不到的地方想你！${COUPON_NAME} ${COUPON_URL} 送你，约吗？来自【${PROD_NAME}】的告白 回T退	3
            String smsContent = x.getSmsTemplateContent();
            smsContent = smsContent.replace("${COUPON_NAME}", "|优惠券名称|");
            smsContent = smsContent.replace("${COUPON_URL}", "|优惠券短链|");
            smsContent = smsContent.replace("${PROD_NAME}", "|推荐的商品名|");
            x.setSmsTemplateContent(smsContent);
        });
        return userGroupList;
    }

    @Override
    public int validGroupTemplate(String headId, String stage) {
        return activityUserGroupMapper.validGroupTemplate(headId, stage);
    }

    /**
     * 保存群组的初始化信息
     * @param headId
     * @param hasPreheat
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveGroupData(String headId, String hasPreheat) {
        List<ActivityGroup> dataList = Lists.newArrayList();
        dataList.add(new ActivityGroup(1L,Long.valueOf(headId),"成长用户","在","活跃"));
        dataList.add(new ActivityGroup(2L,Long.valueOf(headId),"成长用户","在","留存"));
        dataList.add(new ActivityGroup(3L,Long.valueOf(headId),"成长用户","在","流失预警"));
        dataList.add(new ActivityGroup(4L,Long.valueOf(headId),"成长用户","不在",""));

        dataList.add(new ActivityGroup(5L,Long.valueOf(headId),"潜在用户","在","活跃"));
        dataList.add(new ActivityGroup(6L,Long.valueOf(headId),"潜在用户","在","留存"));
        dataList.add(new ActivityGroup(7L,Long.valueOf(headId),"潜在用户","在","流失预警"));

        dataList.add(new ActivityGroup(8L,Long.valueOf(headId),"潜在用户","不在",""));

        //预售
        List<ActivityGroup> preheatList = Lists.newArrayList();
        //正式
        List<ActivityGroup> formalList = Lists.newArrayList();
        dataList.stream().forEach(x->{
            try {
                preheatList.add((ActivityGroup) x.clone());
                formalList.add((ActivityGroup) x.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
        //正式 设置阶段标记
        formalList.stream().forEach(x->x.setActivityStage("formal"));
        //如果包含预售 再写一份预售的数据
        if("1".equalsIgnoreCase(hasPreheat)) {
            preheatList.stream().forEach(x->x.setActivityStage("preheat"));
            formalList.addAll(preheatList);
        }
        activityUserGroupMapper.saveGroupData(formalList);
    }
}
