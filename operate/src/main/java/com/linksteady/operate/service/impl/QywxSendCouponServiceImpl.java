package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.thrift.RetentionData;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.operate.dao.QywxSendCouponMapper;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.SendCouponRecord;
import com.linksteady.operate.service.QywxSendCouponService;
import com.linksteady.operate.vo.CouponInfoVO;
import com.linksteady.operate.vo.SendCouponResultVO;
import com.linksteady.operate.vo.SendCouponVO;
import com.linksteady.smp.starter.domain.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class QywxSendCouponServiceImpl implements QywxSendCouponService {

    @Autowired(required = false)
    QywxSendCouponMapper qywxSendCouponMapper;

    private ReentrantLock lock = new ReentrantLock();

    @Autowired
    ConfigService configService;
    //批量发券
    private static final String SEND_COUPON_BATCH_PATH="/coupon/sendCouponBatch";
    //针对单独发券
    private static final String SEND_COUPON_SINGLE_PATH="/coupon/sendCouponSingle";

    /**
     * 批量生成优惠券发放流水号
     * @param count   生成流水号的个数
     * @return
     */
    private List<String> getCouponNum(CouponInfoVO couponInfoVO,int count){
        lock.lock();
        List<String> couponSnList= null;
        try {
            //获取当前的流水号
            couponSnList= qywxSendCouponMapper.getCouponSnList(couponInfoVO.getCouponId(),count);
            //获取到的券流水号可能数量不够
            if(couponSnList==null||couponSnList.size()!=count)
            {
                throw new Exception("优惠券流水号数量不足");
            }
            //将查询的集合更新usedFlag为Y，并将couponIdentity更新到表中
            qywxSendCouponMapper.updateCouponSnList(couponSnList,couponInfoVO.getCouponId());
            return couponSnList;
        } catch (Exception e) {
            log.error("获取优惠券流水号失败", e);
            return Lists.newArrayList();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 针对个人发放优惠券
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SendCouponResultVO sendCouponToUser(CouponInfoVO couponInfoVO,SendCouponVO sendCouponVO) throws Exception{
        //发券的唯一标记类型 (PHONE表示手机号 UNIONID表示基于unionid发券 默认为PHONE)
        String sendCouponIdentityType=configService.getValueByName(ConfigEnum.sendCouponIdentityType.getKeyCode());
        if(StringUtils.isEmpty(sendCouponIdentityType)){
            sendCouponIdentityType="PHONE";
        }

        //todo 对券信息 和用户信息进行校验
        if(StringUtils.isEmpty(sendCouponVO.getUserIdentity())){
            log.error("发券校验失败，用户标记为空");
            throw new Exception("发券校验失败");
        }

        String sendResult= "";//发券结果
        String sendResultDesc= "";//发券结果描述

        try {
            //根据优惠券编号获取发放流水号
            List<String> couponNoList=getCouponNum(couponInfoVO,1);
            if(couponNoList.size()<=0){
                throw new Exception("发放优惠券失败,获取优惠券流水号失败！");
            }

            sendCouponVO.setCouponSn(couponNoList.get(0));
            String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
            String signature= SHA1.gen(timestamp,JSON.toJSONString(couponInfoVO),JSON.toJSONString(sendCouponVO),sendCouponIdentityType);
            Map<String,String> param= Maps.newHashMap();
            param.put("timestamp", timestamp);
            param.put("couponInfo",JSON.toJSONString(couponInfoVO));
            param.put("sendCouponInfo",JSON.toJSONString(sendCouponVO));
            param.put("sendCouponIdentityType",sendCouponIdentityType);
            param.put("signature", signature);
            log.info("发送单人优惠券，传入的参数为:{}",param);
            //调用发券接口
            String result= OkHttpUtil.postRequestByFormBody(configService.getValueByName(ConfigEnum.sendCouponUrl.getKeyCode())+SEND_COUPON_SINGLE_PATH,param);
            JSONObject jsonObject = JSON.parseObject(result);
            if(null==jsonObject||200!=jsonObject.getIntValue("code")){
                sendResult="F";
                sendResultDesc=jsonObject.getString("msg");
            }else{
                sendResult="S";
                sendResultDesc="发送成功";
                //返回成功后，对象中携带了券编号
                sendCouponVO = JSONObject.parseObject(jsonObject.getString("data"), SendCouponVO.class);
                log.info("单人发券返回信息{}",sendCouponVO.toString());
            }
        }catch (Exception e){
            sendResult="F";
            sendResultDesc="调用单人发券接口失败";
        }
        log.info("保存发券记录");
        SendCouponRecord sendCouponRecord=new SendCouponRecord();
        sendCouponRecord.setCouponInfo(JSON.toJSONString(couponInfoVO));
        sendCouponRecord.setUserInfo(JSON.toJSONString(sendCouponVO));
        sendCouponRecord.setSendResult(sendResult);
        sendCouponRecord.setSendResultDesc(sendResultDesc);
        sendCouponRecord.setInsertDt(LocalDateTime.now());
        sendCouponRecord.setBusinessType(sendCouponVO.getBusinessType());
        sendCouponRecord.setBusinessId(sendCouponVO.getBusinessId());

        //记录发送记录
        qywxSendCouponMapper.saveSendCouponRecord(sendCouponRecord);

        List<SendCouponVO> sendCouponVOList=new ArrayList();
        sendCouponVOList.add(sendCouponVO);
        return new SendCouponResultVO(sendResult,sendCouponRecord.getSendRecordId(),sendCouponVOList);

//        //将发券记录ID更新到每日运营明细表中去
//        qywxSendCouponMapper.updateCouponSendRecord(sendResult,sendCouponRecord.getSendRecordId(),backSendCouponVOList);
//        return flag;
    }

    /**
     * 批量发券接口 一般认为批量发券，同一批，要么都成功了，要么都失败了
     * @param couponInfoVO
     * @param couponInfoVO
     * @param sendCouponVOList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SendCouponResultVO sendCouponBatch(CouponInfoVO couponInfoVO, List<SendCouponVO> sendCouponVOList)  throws Exception{
        //发券的唯一标记类型 (PHONE表示手机号 UNIONID表示基于unionid发券 默认为PHONE)
        String sendCouponIdentityType=configService.getValueByName(ConfigEnum.sendCouponIdentityType.getKeyCode());
        if(StringUtils.isEmpty(sendCouponIdentityType)){
            sendCouponIdentityType="PHONE";
        }

        //todo 对优惠券信息进行校验
        if(sendCouponVOList.stream().filter(i->StringUtils.isEmpty(i.getUserIdentity())).count()>0l){
            log.error("发券校验失败，用户标识为空");
            throw new Exception("发券校验失败");
        }
        // businessId  businessType 不能为空
        // couponID   couponIdentity couponName beginDt endDt 不能为空

        //发券是否成功 true表示成功 false表示失败
        String couponInfo= null;
        String sendCouponList= null;
        String sendResult= "";
        String sendResultDesc= "";

        try {
            //批量获取发放流水号，并放入集合中
            List<String> couponNoList=getCouponNum(couponInfoVO,sendCouponVOList.size());
            if(couponNoList.size()<=0){
                throw new Exception("发放优惠券失败,获取优惠券流水号失败！");
            }
            int i=0;
            for(SendCouponVO sendCouponVO:sendCouponVOList){
                sendCouponVO.setCouponSn(couponNoList.get(i));
                i++;
            }
            String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
            couponInfo = JSON.toJSONString(couponInfoVO);
            sendCouponList = JSON.toJSONString(sendCouponVOList);
            String signature= SHA1.gen(timestamp,couponInfo,sendCouponList,sendCouponIdentityType);

            Map<String,String> param= Maps.newHashMap();
            param.put("timestamp", timestamp);
            param.put("couponInfo",couponInfo);
            param.put("sendCouponList",sendCouponList);
            param.put("sendCouponIdentityType",sendCouponIdentityType);
            param.put("signature", signature);

            log.info("发送优惠券，传入的参数为:{}",param);
            //调用发券接口
            String result= OkHttpUtil.postRequestByFormBody(configService.getValueByName(ConfigEnum.sendCouponUrl.getKeyCode())+SEND_COUPON_BATCH_PATH,param);
            JSONObject jsonObject = JSON.parseObject(result);

            log.info("调用发券服务，接口返回的结果为:{}",jsonObject);

            if(null==jsonObject||200!=jsonObject.getIntValue("code")){
                sendResult="F";
                sendResultDesc=jsonObject.getString("msg");
            }else{
                sendResult="S";
                sendResultDesc="发送成功";
            }
        } catch (Exception e) {
            log.error("调用发券接口进行发券失败，原因为{}",e);
            sendResult="F";
            sendResultDesc="调用发券接口失败";
        }
        SendCouponRecord sendCouponRecord=new SendCouponRecord();
        sendCouponRecord.setCouponInfo(couponInfo);
        sendCouponRecord.setUserInfo(sendCouponList);
        sendCouponRecord.setSendResult(sendResult);
        sendCouponRecord.setSendResultDesc(sendResultDesc);
        sendCouponRecord.setInsertDt(LocalDateTime.now());
        //填充第一个用户的记录
        sendCouponRecord.setBusinessType(sendCouponVOList.get(0).getBusinessType());
        sendCouponRecord.setBusinessId(sendCouponVOList.get(0).getBusinessId());

        log.info("保存发券记录"+JSON.toJSONString(sendCouponVOList));
        //记录发券批次记录
        qywxSendCouponMapper.saveSendCouponRecord(sendCouponRecord);

        return new SendCouponResultVO(sendResult,sendCouponRecord.getSendRecordId(),sendCouponVOList);
//        //将发券记录ID更新到每日运营明细表中去
//        qywxSendCouponMapper.updateCouponSendRecord(sendResult,sendCouponRecord.getSendRecordId(),sendCouponVOList);

    }


}
