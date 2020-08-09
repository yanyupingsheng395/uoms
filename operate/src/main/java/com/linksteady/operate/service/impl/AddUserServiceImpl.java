package com.linksteady.operate.service.impl;

import com.linksteady.common.domain.Ztree;
import com.google.common.base.Splitter;
import com.linksteady.operate.dao.AddUserMapper;
import com.linksteady.operate.domain.AddUserConfig;
import com.linksteady.operate.domain.AddUserHead;
import com.linksteady.operate.domain.QywxParam;
import com.linksteady.operate.service.AddUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author hxcao
 * @date 2020/7/16
 */
@Service
@Slf4j
public class AddUserServiceImpl implements AddUserService {

    @Autowired
    private AddUserMapper addUserMapper;

    @Override
    public int getHeadCount() {
        return addUserMapper.getHeadCount();
    }

    @Override
    public List<AddUserHead> getHeadPageList(int limit, int offset) {
        return addUserMapper.getHeadPageList(limit, offset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveData(AddUserHead addUserHead) {
        addUserHead.setTaskStatus("edit");
        addUserMapper.saveHeadData(addUserHead);
        try {
            filterUsers(addUserHead.getId(), addUserHead.getSourceId(), addUserHead.getRegionId());
        } catch (Exception e) {
            log.error("计算数据出错", e);
        }
    }

    @Override
    public void deleteTask(String id) {
        addUserMapper.deleteHead(id);
    }

    @Override
    public void editConfig(AddUserConfig addUserConfig) {
        addUserMapper.editConfig(addUserConfig);
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
        //推送总人数
        count = addUserMapper.getAddUserListCount(headId);

        //获取默认的 每日推送人数 及 推送转化率
        int defaultAddcount;
        double defaultApplyRate;
        QywxParam qywxParam = addUserMapper.getQywxParam();
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
            dailyAddNum = (int) Math.floor(defaultAddcount * defaultApplyRate);
            waitDays = count / defaultAddcount;
            addTotal = (int) Math.floor(defaultApplyRate * count);
        }
        //更新记录
        addUserMapper.updatePushParameter(headId, count, defaultAddcount, defaultApplyRate, dailyAddNum, waitDays, addTotal);
    }

    /**
     * 执行一次推送任务
     *
     * @param headId
     */
    @Override
    public void execTask(long headId) throws Exception {
        //判断任务的状态

        //判断任务的剩余人数

        //判断当天是否已经有推送计划

        //写入推送计划

        //更新推送明细

        //更新主记录表的剩余人数

        //将推送明细数据放到短信表里面去


    }

    @Override
    public void saveDailyUserData(String headId, String dailyUserCnt, String dailyApplyRate) {
        Integer dailyUserCntLong = Integer.parseInt(dailyUserCnt);
        Double dailyApplyRateDouble = Double.parseDouble(dailyApplyRate);
        int count = addUserMapper.getAddUserListCount(Long.parseLong(headId));
        int dailyAddNum = 0;
        int waitDays = 0;
        int addTotal = 0;
        //计算预计每日添加好友人数  预计全部推送所需天数  预计添加好友总人数
        if (count > 0) {
            dailyAddNum = (int) Math.floor(dailyUserCntLong * dailyApplyRateDouble);
            waitDays = count / dailyUserCntLong;
            addTotal = (int) Math.floor(dailyApplyRateDouble * count);
        }
        //更新记录
        addUserMapper.updatePushParameter(Long.parseLong(headId), count, dailyUserCntLong, dailyApplyRateDouble, dailyAddNum, waitDays, addTotal);
    }

    @Override
    public AddUserHead getHeadById(long id) {
        return addUserMapper.getHeadById(id);
    }

}
