package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.operate.dao.ActivityCovMapper;
import com.linksteady.operate.domain.ActivityCovInfo;
import com.linksteady.operate.domain.enums.ActivityStageEnum;
import com.linksteady.operate.service.ActivityCovService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/2/26
 */
@Service
public class ActivityCovServiceImpl implements ActivityCovService {

    @Autowired
    private ActivityCovMapper activityCovMapper;

    @Override
    public ActivityCovInfo geConvertInfo(String headId, String stage) {
        String sql = "";
        if (ActivityStageEnum.preheat.getStageCode().equals(stage)) {
            sql = "select ACTIVITY_HEAD_ID,PREHEAT_NOTIFY_COVID,PREHEAT_NOTIFY_COV,PREHEAT_NOTIFY_PUSHNUM,PREHEAT_NOTIFY_COVNUM from UO_OP_ACTIVITY_COVINFO where ACTIVITY_HEAD_ID = '" + headId + "'";
        }
        if (ActivityStageEnum.formal.getStageCode().equals(stage)) {
            sql = "select ACTIVITY_HEAD_ID,NORMAL_NOTIFY_COVID,NORMAL_NOTIFY_COV,NORMAL_NOTIFY_PUSHNUM,NORMAL_NOTIFY_COVNUM from UO_OP_ACTIVITY_COVINFO where ACTIVITY_HEAD_ID = '" + headId + "'";
        }
        List<ActivityCovInfo> covInfos = activityCovMapper.getCovInfo(sql);
        return covInfos.size() > 0 ? covInfos.get(0) : new ActivityCovInfo();
    }

    @Override
    public List<ActivityCovInfo> getCovList() {
        return activityCovMapper.getCovList(null);
    }

    @Override
    public void insertCovInfo(String headId, String covListId, String stage) {
        activityCovMapper.insertCovInfo(headId, covListId, stage);
    }

    @Override
    public List<Map<String, String>> calculateCov(String headId, String stage, String changedCovId, String defaultCovId) {
        DecimalFormat df = new DecimalFormat("#.##");
        List<Map<String, String>> result = Lists.newArrayList();
        ActivityCovInfo activityCovInfo1 = activityCovMapper.getCovInfoById(changedCovId);
        ActivityCovInfo activityCovInfo2 = activityCovMapper.getCovInfoById(defaultCovId);
        double rate_val = Math.abs((Double.parseDouble(activityCovInfo1.getCovRate()) - Double.parseDouble(activityCovInfo2.getCovRate())));
        int push_val = Math.abs((Integer.parseInt(activityCovInfo1.getExpectPushNum()) - Integer.parseInt(activityCovInfo2.getExpectPushNum())));
        int cov_val = Math.abs((Integer.parseInt(activityCovInfo1.getExpectCovNum()) - Integer.parseInt(activityCovInfo2.getExpectCovNum())));

        String rate_per = Double.parseDouble(activityCovInfo2.getCovRate()) == 0D ? "0" : df.format(rate_val / Double.parseDouble(activityCovInfo2.getCovRate()));
        String push_per = Double.parseDouble(activityCovInfo2.getExpectPushNum()) == 0D ? "0" : df.format(push_val / Double.parseDouble(activityCovInfo2.getExpectPushNum()));
        String cov_per = Double.parseDouble(activityCovInfo2.getExpectCovNum()) == 0D ? "0" : df.format(cov_val / Double.parseDouble(activityCovInfo2.getExpectCovNum()));

        Map<String, String> map1 = Maps.newHashMap();
        map1.put("name", "改变方案对转化率造成的预期改变");
        map1.put("val", df.format(rate_val));
        map1.put("per", String.valueOf(rate_per));

        Map<String, String> map2 = Maps.newHashMap();
        map2.put("name", "改变方案对推送用户数造成的预期改变");
        map2.put("val", String.valueOf(push_val));
        map2.put("per", String.valueOf(push_per));

        Map<String, String> map3 = Maps.newHashMap();
        map3.put("name", "改变方案对转化用户数造成的预期改变");
        map3.put("val", String.valueOf(cov_val));
        map3.put("per", String.valueOf(cov_per));

        result.add(map1);
        result.add(map2);
        result.add(map3);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCovInfo(long headId, String stage, String covId) {
        if (ActivityStageEnum.preheat.getStageCode().equals(stage)) {
            activityCovMapper.updatePreheatCovInfo(headId, covId);
        }
        if (ActivityStageEnum.formal.getStageCode().equals(stage)) {
            activityCovMapper.updateFormalCovInfo(headId, covId);
        }
    }
}
