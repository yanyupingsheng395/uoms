package com.linksteady.qywx.service.impl;

import com.linksteady.qywx.dao.QywxParamMapper;
import com.linksteady.qywx.domain.QywxParam;
import com.linksteady.qywx.service.QywxParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;

/**
 * 企业微信参数表
 *
 * @author hxcao
 * @date 2019-07-31
 */
@Service
public class QywxParamServiceImpl implements QywxParamService {

    @Autowired
    private QywxParamMapper qywxParamMapper;


    @Override
    public QywxParam getQywxParam() {
        return qywxParamMapper.getQywxParam();
    }

    /**
     * 更新每日加人上限、转化率 (调用此方法请在controller层加锁防止并发)
     * @param dailyAddNum
     * @param dailyAddRate
     */
    @Override
    @Transactional
    public QywxParam updateQywxParam(int dailyAddNum, double dailyAddRate,String opUser,int version) throws Exception{
        int count=qywxParamMapper.updateVersion(version);
        if(count==0)
        {
            throw new OptimisticLockException("配置数据已被修改，请返回列表界面重试！");
        }
        //计算可被推送的总人数
        int applyNum=(int)Math.floor(dailyAddNum/dailyAddRate*100);

        QywxParam qywxParam=qywxParamMapper.getQywxParam();
        int triggerNum=qywxParam.getTriggerNum();
        if(applyNum<triggerNum)
        {
            throw new Exception("保存错误，可发送总人数小于自动触发人数!");
        }

        //更新到数据库
        qywxParamMapper.updateQywxParam(dailyAddNum,dailyAddRate,applyNum,opUser);

        return qywxParamMapper.getQywxParam();

    }

    /**
     * 更新 主动触达人数(调用此方法请在controller层加锁防止并发)
     * @param triggerNum
     */
    @Override
    @Transactional
    public QywxParam updateTriggerNum(int triggerNum,String opUser,int version) throws Exception{
        int count=qywxParamMapper.updateVersion(version);

        if(count==0)
        {
            throw new OptimisticLockException("配置数据已被修改，请返回列表界面重试！");
        }
        QywxParam qywxParam=qywxParamMapper.getQywxParam();
        //每日可申请的总人数
        int applyNum=qywxParam.getDailyApplyNum();

        if(triggerNum>applyNum)
        {
            throw new Exception("更新错误，自动触发人数大于可发送的总人数！");
        }
        //更新到数据库
        qywxParamMapper.updateTriggerNum(triggerNum,opUser);

        return qywxParamMapper.getQywxParam();
    }
}
