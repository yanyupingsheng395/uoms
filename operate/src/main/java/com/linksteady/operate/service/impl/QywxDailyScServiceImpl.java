package com.linksteady.operate.service.impl;

import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.QywxDailyMapper;
import com.linksteady.operate.exception.SendCouponException;
import com.linksteady.operate.service.QywxDailyScService;
import com.linksteady.operate.service.QywxSendCouponService;
import com.linksteady.operate.vo.CouponInfoVO;
import com.linksteady.operate.vo.SendCouponResultVO;
import com.linksteady.operate.vo.SendCouponVO;
import com.linksteady.operate.vo.couponSnCountVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QywxDailyScServiceImpl implements QywxDailyScService {

    @Autowired
    ConfigService configService;

    @Autowired
    QywxSendCouponService qywxSendCouponService;

    @Autowired
    private QywxDailyMapper qywxDailyMapper;

    /***
     * 对每日运营用户进行发券
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,noRollbackFor=SendCouponException.class)
    public void sendCouponToDailyUser(Long headId) throws SendCouponException
    {
        String sendCouponIdentityType=configService.getValueByName(ConfigEnum.sendCouponIdentityType.getKeyCode());
        if(org.springframework.util.StringUtils.isEmpty(sendCouponIdentityType)){
            sendCouponIdentityType="PHONE";
        }
        String identityColumn="PHONE".equals(sendCouponIdentityType)?"user_phone":"unionid";

        //获取当前head下有多少种券
        List<CouponInfoVO> couponInfoVOList=qywxDailyMapper.getCouponList(headId);
        Long couponId=null;
        List<SendCouponVO> sendCouponVOList=null;

        //对优惠券流水号数量进行校验
        List<couponSnCountVO> couponSnCountVOList=qywxSendCouponService.getCouponSnCount();
        Map<Long,Integer> snCountMap=couponSnCountVOList.stream().collect(Collectors.toMap(couponSnCountVO::getCouponId, couponSnCountVO::getSnCount));

        StringJoiner stringJoiner=new StringJoiner(",", "[", "]");
        for(CouponInfoVO couponInfoVO:couponInfoVOList)
        {
            int snCount=snCountMap.get(couponInfoVO.getCouponId())==null?0:snCountMap.get(couponInfoVO.getCouponId()).intValue();
            if(couponInfoVO.getUserCount()>snCount)
            {
                stringJoiner.add(couponInfoVO.getCouponName());
            }
        }
        //判断
        if(stringJoiner.toString().length()>2)
        {
            throw new SendCouponException("以下券的券编码不足:"+stringJoiner.toString());
        }

        for(CouponInfoVO couponInfoVO:couponInfoVOList)
        {
            couponId=couponInfoVO.getCouponId();
            //设置券有效期开始时间
            couponInfoVO.setBeginDate(LocalDate.now());
            //查询当前这个券下面有多少人
            int count=qywxDailyMapper.getCouponUserCount(headId,couponId);

            if(count<=100)
            {
                //直接调接口进行优惠券发放
                sendCouponVOList=qywxDailyMapper.getCouponUserList(headId,couponId,100,0,identityColumn);
                try {
                    SendCouponResultVO sendCouponResultVO =qywxSendCouponService.sendCouponBatch(couponInfoVO,sendCouponVOList);
                    log.info("每日运营发券，返回的结果为:{}",sendCouponResultVO);
                    //将发券信息更新到每日运营明细表
                    qywxDailyMapper.updateCouponSendInfo(sendCouponResultVO.getSendRecordId(),sendCouponResultVO.getSendCouponVOList());
                } catch (Exception e) {
                    log.error("每日运营发送优惠券失败，原因为{}",e);
                    throw new SendCouponException(e.getMessage());
                }
            }else
            {
                //进行分页
                int pageSize=100;
                int page = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
                //发券是否成功 true表示成功 false表示失败
                boolean scFlag=true;
                String  errmsg="";

                for (int i = 0; i < page; i++) {
                    sendCouponVOList=qywxDailyMapper.getCouponUserList(headId,couponId,pageSize,i * pageSize,identityColumn);
                    try {
                        SendCouponResultVO sendCouponResultVO =qywxSendCouponService.sendCouponBatch(couponInfoVO,sendCouponVOList);
                        //将发券信息更新到每日运营明细表
                        qywxDailyMapper.updateCouponSendInfo(sendCouponResultVO.getSendRecordId(),sendCouponResultVO.getSendCouponVOList());
                        log.info("每日运营发券，返回的结果为:{}",sendCouponResultVO);
                    } catch (Exception e) {
                        log.error("每日运营发送优惠券失败，原因为{}",e);
                        scFlag=false;
                        errmsg=e.getMessage();
                    }
                }

                if(scFlag==false)
                {
                    throw new SendCouponException(errmsg);
                }
            }
        }
    }
}
