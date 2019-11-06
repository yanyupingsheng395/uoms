package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.operate.dao.ActivityUserGroupMapper;
import com.linksteady.operate.dao.ActivityUserMapper;
import com.linksteady.operate.domain.ActivityGroup;
import com.linksteady.operate.service.ActivityUserGroupService;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
}
