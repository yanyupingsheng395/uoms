package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.operate.dao.QywxSendCouponMapper;
import com.linksteady.operate.domain.SendCouponRecord;
import com.linksteady.operate.service.QywxSendCouponService;
import com.linksteady.operate.vo.CouponInfoVO;
import com.linksteady.operate.vo.SendCouponVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class QywxSendCouponServiceImpl implements QywxSendCouponService {

    @Autowired
    QywxSendCouponMapper qywxSendCouponMapper;

    @Autowired
    ConfigService configService;

    private static final String SEND_COUPON_BATCH_PATH="/coupon/sendCouponBatch";

    /**
     * 在新的事务中执行，避免 消息发送 优惠券发送 互相干扰
     * @param headId
     * @return 发券是否成功 true表示成功 false表示失败
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendCouponToUser(Long headId) {
        //获取当前head下有多少种券
        List<CouponInfoVO> couponInfoVOList=qywxSendCouponMapper.getCouponList(headId);
        Long couponId=null;
        List<SendCouponVO> sendCouponVOList=null;

        //发券是否成功 true表示成功 false表示失败
        boolean sendCouponSuccess=true;
        for(CouponInfoVO couponInfoVO:couponInfoVOList)
        {
            couponId=couponInfoVO.getCouponId();
            //查询当前这个券下面有多少人
            int count=qywxSendCouponMapper.getCouponUserCount(headId,couponId);

            if(count<=100)
            {
                //直接调接口进行优惠券发放
                sendCouponVOList=qywxSendCouponMapper.getCouponUserList(headId,couponId,100,0);
                sendCouponSuccess=sendCoupon(headId,couponInfoVO,sendCouponVOList);
            }else
            {
                //进行分页
                int pageSize=100;
                int page = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);

                boolean tempFlag=true;
                for (int i = 0; i < page; i++) {
                    sendCouponVOList=qywxSendCouponMapper.getCouponUserList(headId,couponId,pageSize,i * pageSize);
                    tempFlag=sendCoupon(headId,couponInfoVO,sendCouponVOList);

                    if(!tempFlag)
                    {
                        sendCouponSuccess=false;
                    }
                }

            }
        }
        qywxSendCouponMapper.updateSendCouponFlag(headId);

        return sendCouponSuccess;
    }

    /**
     * 调用接口进行优惠券发放 并记录发送记录
     */
    private boolean sendCoupon(long headId,CouponInfoVO couponInfoVO,List<SendCouponVO> sendCouponVOList)
    {
        //发券是否成功 true表示成功 false表示失败
        boolean flag=true;
        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
        String couponInfo= JSON.toJSONString(couponInfoVO);
        String sendCouponList=JSON.toJSONString(sendCouponVOList);
        String signature= SHA1.gen(timestamp,couponInfo,sendCouponList);

        Map<String,String> param= Maps.newHashMap();
        param.put("timestamp", timestamp);
        param.put("couponInfo",couponInfo);
        param.put("sendCouponList",sendCouponList);
        param.put("signature", signature);

        //推送到用户成长端
        String result= OkHttpUtil.postRequestByFormBody(configService.getValueByName(ConfigEnum.sendCouponUrl.getKeyCode())+SEND_COUPON_BATCH_PATH,param);
        JSONObject jsonObject = JSON.parseObject(result);

        String sendResult="";
        String sendResultDesc="";
        /**
         * 接口调用返回的SendCouponVOList 如果成功调用，则相比于传过去的数据，多了券号字段
         */
        List<SendCouponVO> backSendCouponVOList=null;
        if(null==jsonObject||200!=jsonObject.getIntValue("code"))
        {
            sendResult="F";
            sendResultDesc=jsonObject.getString("msg");
            backSendCouponVOList=sendCouponVOList;
            flag=false;
        }else
        {
            sendResult="S";
            sendResultDesc="发送成功";
            backSendCouponVOList= JSONObject.parseArray(jsonObject.getString("data"), SendCouponVO.class);
        }

        SendCouponRecord sendCouponRecord=new SendCouponRecord();
        sendCouponRecord.setHeadId(headId);
        sendCouponRecord.setCouponInfo(couponInfo);
        sendCouponRecord.setUserInfo(sendCouponList);
        sendCouponRecord.setSendResult(sendResult);
        sendCouponRecord.setSendResultDesc(sendResultDesc);
        sendCouponRecord.setInsertDt(LocalDateTime.now());

        log.info(JSON.toJSONString(backSendCouponVOList));
        //记录发送记录
        qywxSendCouponMapper.saveSendCouponRecord(sendCouponRecord);
        //将发券记录ID更新到每日运营明细表中去
        qywxSendCouponMapper.updateCouponSendRecord(sendResult,sendCouponRecord.getSendRecordId(),backSendCouponVOList);

        return flag;
    }

}
