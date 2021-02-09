package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.linksteady.common.domain.enums.ConfigEnum;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.operate.dao.QywxSendCouponMapper;
import com.linksteady.operate.domain.CouponInfo;
import com.linksteady.operate.domain.SendCouponRecord;
import com.linksteady.operate.service.QywxSendCouponService;
import com.linksteady.operate.vo.CouponInfoVO;
import com.linksteady.operate.vo.SendCouponVO;
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

@Service
@Slf4j
public class QywxSendCouponServiceImpl implements QywxSendCouponService {

    @Autowired(required = false)
    QywxSendCouponMapper qywxSendCouponMapper;

    private static final String COUPON_NO_PREFIX="AYHQ";

    @Autowired
    ConfigService configService;
    //批量发券
    private static final String SEND_COUPON_BATCH_PATH="/coupon/sendCouponBatch";
    //针对单独发券
    private static final String SEND_COUPON_SINGLE_PATH="/coupon/sendCouponSingle";

//    /**
//     * 在新的事务中执行，避免 消息发送 优惠券发送 互相干扰
//     * @param headId
//     * @return 发券是否成功 true表示成功 false表示失败
//     */
//    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public boolean sendCouponToDailyUser(Long headId) {
//         //发券的唯一标记类型 (PHONE表示手机号 UNIONID表示基于unionid发券 默认为PHONE)
//         String sendCouponIdentityType=configService.getValueByName(ConfigEnum.sendCouponIdentityType.getKeyCode());
//         if(StringUtils.isEmpty(sendCouponIdentityType))
//         {
//             sendCouponIdentityType="PHONE";
//         }
//
//        //获取当前head下有多少种券
//        List<CouponInfoVO> couponInfoVOList=qywxSendCouponMapper.getCouponList(headId);
//        Long couponId=null;
//        List<SendCouponVO> sendCouponVOList=null;
//        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        //发券是否成功 true表示成功 false表示失败
//        boolean sendCouponSuccess=true;
//        for(CouponInfoVO couponInfoVO:couponInfoVOList)
//        {
//            couponId=couponInfoVO.getCouponId();
//            //设置券有效期开始时间
//            couponInfoVO.setBeginDate(LocalDate.now());
//            //查询当前这个券下面有多少人
//            int count=qywxSendCouponMapper.getCouponUserCount(headId,couponId);
//
//            if(count<=100)
//            {
//                //直接调接口进行优惠券发放
//                sendCouponVOList=qywxSendCouponMapper.getCouponUserList(headId,couponId,100,0);
//                sendCouponSuccess=sendCouponBatch(couponInfoVO,sendCouponVOList);
//            }else
//            {
//                //进行分页
//                int pageSize=100;
//                int page = count % pageSize == 0 ? count / pageSize : (count / pageSize + 1);
//
//                boolean tempFlag=true;
//                for (int i = 0; i < page; i++) {
//                    sendCouponVOList=qywxSendCouponMapper.getCouponUserList(headId,couponId,pageSize,i * pageSize);
//                    tempFlag=sendCouponBatch(couponInfoVO,sendCouponVOList);
//                    log.info("发券，返回的状态标记为:{}",tempFlag);
//
//                    if(!tempFlag)
//                    {
//                        sendCouponSuccess=false;
//                    }
//                }
//            }
//        }
//        return sendCouponSuccess;
//    }

    /**
     * 批量生成优惠券发放流水号
     * @param count           生成流水号的个数
     * @return
     */
    private synchronized List<String> getCouponNum(long couponId,int count,String couponIdentity){
       //获取当前的流水号
        List<String> couponSnList= Lists.newArrayList();
        couponSnList= qywxSendCouponMapper.getCouponSnList(couponId,count);
        //将查询的集合更新usedFlag为Y，并将couponIdentity更新到表中
        qywxSendCouponMapper.updateCouponSnList(couponSnList,couponIdentity,couponId);
        return couponSnList;
    }

    /**
     * 针对个人发放优惠券
     * @return
     */
    @Override
    public boolean sendCouponToUser(Long couponId,String couponIdentity,String userIdentity) {
        //发券的唯一标记类型 (PHONE表示手机号 UNIONID表示基于unionid发券 默认为PHONE)
        String sendCouponIdentityType=configService.getValueByName(ConfigEnum.sendCouponIdentityType.getKeyCode());
        if(StringUtils.isEmpty(sendCouponIdentityType)){
            sendCouponIdentityType="PHONE";
        }
        //发券是否成功 true表示成功 false表示失败
        boolean flag=true;
        String sendResult= "";//发券结果
        String sendResultDesc= "";//发券结果描述
        String couponInfo= null;
        SendCouponVO sendCouponVO=new SendCouponVO();
        CouponInfoVO couponInfoVO=qywxSendCouponMapper.queryCoupon(couponId);
        //设置券有效期开始时间
        couponInfoVO.setBeginDate(LocalDate.now());
        //设置券有效期结束时间
        DateTimeFormatter ftf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String couponEndDate = qywxSendCouponMapper.getCouponEndDate(couponIdentity);
        couponInfoVO.setEndDate(LocalDate.parse(couponEndDate, ftf1));
        //进行数据校验
        if("PHONE".equals(sendCouponIdentityType)){
            if(StringUtils.isEmpty(userIdentity)){
                log.error("发券校验失败，发券方式为基于手机号发券，但获取到的手机号为空");
                return false;
            }else{
                sendCouponVO.setUserPhone(userIdentity);
            }
        }else if("UNIONID".equals(sendCouponIdentityType)){
            if(StringUtils.isEmpty(userIdentity)){
                log.error("发券校验失败，发券方式为基于unionid发券，但获取到的unionid为空");
                return false;
            }else{
                sendCouponVO.setUnionId(userIdentity);
            }
        }
        List<SendCouponVO> backSendCouponVOList= new ArrayList<>();
        try {
            couponInfo = JSON.toJSONString(couponInfoVO);
            //根据优惠券编号获取发放流水号
            List<String> couponNoList=getCouponNum(couponId,1,couponInfoVO.getCouponIdentity());
            String couponSn=couponNoList.get(0);
            String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
            String signature= SHA1.gen(timestamp,couponInfo,userIdentity,couponSn,sendCouponIdentityType);
            Map<String,String> param= Maps.newHashMap();
            param.put("timestamp", timestamp);
            param.put("couponInfo",couponInfo);
            param.put("userIdentity",userIdentity);
            param.put("couponSn",couponSn);
            param.put("sendCouponIdentityType",sendCouponIdentityType);
            param.put("signature", signature);
            log.info("发送单人优惠券，传入的参数为:{}",param);
            //调用发券接口
            String result= OkHttpUtil.postRequestByFormBody(configService.getValueByName(ConfigEnum.sendCouponUrl.getKeyCode())+SEND_COUPON_SINGLE_PATH,param);
            JSONObject jsonObject = JSON.parseObject(result);
            if(null==jsonObject||200!=jsonObject.getIntValue("code")){
                sendResult="F";
                sendResultDesc=jsonObject.getString("msg");
                backSendCouponVOList.add(sendCouponVO);
                flag=false;
            }else{
                sendResult="S";
                sendResultDesc="发送成功";
                //返回成功后，对象中携带了券编号
                sendCouponVO = JSONObject.parseObject(jsonObject.getString("data"), SendCouponVO.class);
                backSendCouponVOList.add(sendCouponVO);
                log.info("单人发券返回信息{}",sendCouponVO.toString());
            }
        }catch (Exception e){
            sendResult="F";
            sendResultDesc="调用单人发券接口失败";
            backSendCouponVOList.add(sendCouponVO);
            flag=false;
        }
        log.info("保存发券记录");
        SendCouponRecord sendCouponRecord=new SendCouponRecord();
        sendCouponRecord.setCouponInfo(JSON.toJSONString(couponInfoVO));
        sendCouponRecord.setUserInfo(JSON.toJSONString(sendCouponVO));
        sendCouponRecord.setSendResult(sendResult);
        sendCouponRecord.setSendResultDesc(sendResultDesc);
        sendCouponRecord.setInsertDt(LocalDateTime.now());

        //记录发送记录
        qywxSendCouponMapper.saveSendCouponRecord(sendCouponRecord);
        //将发券记录ID更新到每日运营明细表中去
        qywxSendCouponMapper.updateCouponSendRecord(sendResult,sendCouponRecord.getSendRecordId(),backSendCouponVOList);
        return flag;
    }

    @Override
    public boolean sendCouponBatch(long couponId,CouponInfoVO couponInfoVO, List<SendCouponVO> sendCouponVOList) {
        //发券的唯一标记类型 (PHONE表示手机号 UNIONID表示基于unionid发券 默认为PHONE)
        String sendCouponIdentityType=configService.getValueByName(ConfigEnum.sendCouponIdentityType.getKeyCode());
        if(StringUtils.isEmpty(sendCouponIdentityType)){
            sendCouponIdentityType="PHONE";
        }
        //发券是否成功 true表示成功 false表示失败
        boolean flag=true;
        String couponInfo= null;
        String sendCouponList= null;
        String sendResult= "";
        String sendResultDesc= "";

        //进行数据校验
        if("PHONE".equals(sendCouponIdentityType)){
            if(sendCouponVOList.stream().filter(i->StringUtils.isEmpty(i.getUserPhone())).count()>0l){
                log.error("发券校验失败，发券方式为基于手机号发券，但获取到的手机号为空");
                return false;
            }
        }else if("UNIONID".equals(sendCouponIdentityType)){
            if(sendCouponVOList.stream().filter(i->StringUtils.isEmpty(i.getUnionId())).count()>0l){
                log.error("发券校验失败，发券方式为基于手机号发券，但获取到的手机号为空");
                return false;
            }
        }

        /**
         * 接口调用返回的SendCouponVOList 如果成功调用，则相比于传过去的数据，多了券号字段
         */
        List<SendCouponVO> backSendCouponVOList= null;
        try {
            //批量获取发放流水号，并放入集合中
            List<String> couponNoList=getCouponNum(couponId,sendCouponVOList.size(),couponInfoVO.getCouponIdentity());
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
                backSendCouponVOList=sendCouponVOList;
                flag=false;
            }else{
                sendResult="S";
                sendResultDesc="发送成功";
                backSendCouponVOList= JSONObject.parseArray(jsonObject.getString("data"), SendCouponVO.class);
            }
        } catch (Exception e) {
            log.error("调用发券接口进行发券失败，原因为{}",e);
            sendResult="F";
            sendResultDesc="调用发券接口失败";
            backSendCouponVOList=sendCouponVOList;
            flag=false;
        }
        SendCouponRecord sendCouponRecord=new SendCouponRecord();
        sendCouponRecord.setCouponInfo(couponInfo);
        sendCouponRecord.setUserInfo(sendCouponList);
        sendCouponRecord.setSendResult(sendResult);
        sendCouponRecord.setSendResultDesc(sendResultDesc);
        sendCouponRecord.setInsertDt(LocalDateTime.now());

        log.info("保存发券记录"+JSON.toJSONString(backSendCouponVOList));
        //记录发送记录
        qywxSendCouponMapper.saveSendCouponRecord(sendCouponRecord);
        //将发券记录ID更新到每日运营明细表中去
        qywxSendCouponMapper.updateCouponSendRecord(sendResult,sendCouponRecord.getSendRecordId(),backSendCouponVOList);
        return flag;
    }


}
