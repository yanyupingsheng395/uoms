package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.linksteady.common.bo.UserBo;
import com.linksteady.common.domain.QywxMessage;
import com.linksteady.common.util.FileUtils;
import com.linksteady.operate.dao.QywxActivityPushMapper;
import com.linksteady.operate.dao.QywxManualHeaderMapper;
import com.linksteady.operate.domain.QywxManualDetail;
import com.linksteady.operate.domain.QywxManualError;
import com.linksteady.operate.domain.QywxManualHeader;
import com.linksteady.operate.domain.QywxPushList;
import com.linksteady.operate.exception.LinkSteadyException;
import com.linksteady.operate.exception.OptimisticLockException;
import com.linksteady.operate.service.QywxManualPushService;
import com.linksteady.operate.service.QywxMessageService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class QywxManualPushServiceImpl implements QywxManualPushService {

    @Autowired(required = false)
    private QywxManualHeaderMapper qywxManualHeaderMapper;

    @Autowired
    private QywxMessageService qywxMessageService;

    @Autowired(required = false)
    private QywxActivityPushMapper qywxActivityPushMapper;

    @Override
    public int getHeaderListCount() {
        return qywxManualHeaderMapper.getHeaderListCount();
    }

    @Override
    public List<QywxManualHeader> getHeaderListData(int limit, int offset) {
        return qywxManualHeaderMapper.getHeaderListData(limit,offset);
    }

    @Override
    public String getHeadStatus(Long headId) {
        return qywxManualHeaderMapper.getHeadStatus(headId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushMessage(QywxManualHeader header) throws Exception{
        long headId = header.getHeadId();
        //???????????????2????????????????????????????????????        2????????????????????????0??????????????????
        int count=qywxManualHeaderMapper.updateStatusToPlaning(headId,"2","0",null);
        if(count==0){
            throw new OptimisticLockException();
        }
        //???????????????????????????
        List<QywxManualDetail> detail=qywxManualHeaderMapper.getQywxManualDetail(headId);
        //?????????headiD????????????followuser?????????
        List<String> followerUserIdList = detail.stream().map(QywxManualDetail::getFollowerUserId).distinct().collect(Collectors.toList());
        String appId = qywxMessageService.getMpAppId();
        followerUserIdList.stream().forEach(x->{
            String followerUserId = x;
            //???????????????????????????
            int countDetail = qywxManualHeaderMapper.getQywxManualDetailCount(headId, followerUserId);
            int pageSize = 10000;
            //?????????????????????????????????????????????
            if(countDetail<=pageSize){
                log.info("?????????????????????<=10000");
                if(countDetail>0){
                    //????????????????????????
                    List<QywxManualDetail> qywxManualDetailList = qywxManualHeaderMapper.getQywxManualDetailByPage(headId, followerUserId, 0, 10000);
                    //??????????????????
                    QywxPushList qywxPushList=new QywxPushList();
                    qywxPushList.setTextContent(header.getTextContent());
                    qywxPushList.setMpTitle(header.getMpTitle());
                    qywxPushList.setMpUrl(header.getMpUrl());
                    qywxPushList.setMpMediaId(header.getMpMediald());
                    qywxPushList.setMpAppid(appId);
                    qywxPushList.setPicUrl(header.getPicUrl());
                    qywxPushList.setLinkUrl(header.getLinkUrl());
                    qywxPushList.setLinkDesc(header.getLinkDesc());
                    qywxPushList.setLinkPicurl(header.getLinkPicurl());
                    qywxPushList.setLinkTitle(header.getLinkTitle());
                    qywxPushList.setExternalContactIds(org.apache.commons.lang3.StringUtils.join( qywxManualDetailList.stream().map(QywxManualDetail::getQywxContactId).collect(Collectors.toList()),","));
                    qywxPushList.setFollowUserId(followerUserId);
                    qywxPushList.setSourceId(detail.get(0).getHeadId());
                    qywxManualHeaderMapper.insertPushList(qywxPushList);
                    //?????????????????????
                    pushQywxMsg(qywxPushList,qywxManualDetailList,header);
                }
            }else{
                int pageNum = countDetail % pageSize == 0 ? (countDetail / pageSize) : ((countDetail / pageSize) + 1);
                for (int i = 0; i < pageNum; i++) {
                    log.info("????????????????????????{}???????????????{}", pageSize,i*pageSize);
                    //????????????????????????????????????????????????????????????????????????
                    List<QywxManualDetail> qywxManualDetailList = qywxManualHeaderMapper.getQywxManualDetailByPage(headId, followerUserId, i * pageSize, pageSize);
                    if(qywxManualDetailList.size() > 0) {
                        //??????????????????
                        QywxPushList qywxPushList=new QywxPushList();
                        qywxPushList.setTextContent(header.getTextContent());
                        qywxPushList.setMpTitle(header.getMpTitle());
                        qywxPushList.setMpUrl(header.getMpUrl());
                        qywxPushList.setMpMediaId(header.getMpMediald());
                        qywxPushList.setMpAppid(appId);
                        qywxPushList.setPicUrl(header.getPicUrl());
                        qywxPushList.setLinkUrl(header.getLinkUrl());
                        qywxPushList.setLinkDesc(header.getLinkDesc());
                        qywxPushList.setLinkPicurl(header.getLinkPicurl());
                        qywxPushList.setLinkTitle(header.getLinkTitle());
                        qywxPushList.setExternalContactIds(org.apache.commons.lang3.StringUtils.join(qywxManualDetailList.stream().map(QywxManualDetail::getQywxContactId).collect(Collectors.toList()),","));
                        qywxPushList.setFollowUserId(followerUserId);
                        qywxPushList.setSourceId(detail.get(0).getHeadId());
                        //??????????????????uo_qywx_push_list?????????
                        qywxManualHeaderMapper.insertPushList(qywxPushList);
                        //?????????????????????
                        pushQywxMsg(qywxPushList,qywxManualDetailList,header);
                    }
                }
            }
        });
        LocalDateTime pushtime = LocalDateTime.now();
        //?????????????????????"?????????"
       int failCount=qywxManualHeaderMapper.getPushDetailStatus(headId);
       log.info("????????????????????????{}",failCount);
       String status="1";
       if(failCount>0){
           status="3";
       }
        qywxManualHeaderMapper.updateStatusToPlaning(headId,status,"2",pushtime);
    }

    /**
     * ????????????????????????
     *
     * @param qywxPushList (??????????????????)
     */
    public void pushQywxMsg(QywxPushList qywxPushList, List<QywxManualDetail> qywxManualDetailList,QywxManualHeader header) {
        log.info("????????????{}",qywxPushList.toString());
        log.info("????????????{}",header.toString());
        if(null==qywxManualDetailList||qywxManualDetailList.size()==0){
            qywxActivityPushMapper.updatePushList(qywxPushList.getPushId(),"F","","","??????????????????");
            return;
        }
        //????????????????????????????????????????????????
        boolean flag=true;
        QywxMessage qywxMessage = new QywxMessage();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(qywxPushList.getTextContent())) {
            qywxMessage.setText(qywxPushList.getTextContent());
            flag=false;
        }
        //?????????
        if("applets".equals(header.getMsgType()))
        {
            qywxMessage.setMpTitle(qywxPushList.getMpTitle());
            qywxMessage.setMpPicMediaId(qywxPushList.getMpMediaId());
            qywxMessage.setMpAppid(qywxPushList.getMpAppid());
            qywxMessage.setMpPage(qywxPushList.getMpUrl());
            flag=false;
        }
        //??????
        if("image".equals(header.getMsgType())){
            qywxMessage.setImgPicUrl(qywxPushList.getPicUrl());
            flag=false;
        }
        //??????
        if("web".equals(header.getMsgType())){
            qywxMessage.setLinkTitle(qywxPushList.getLinkTitle());
            qywxMessage.setLinkPicUrl(qywxPushList.getLinkPicurl());
            qywxMessage.setLinkDesc(qywxPushList.getLinkDesc());
            qywxMessage.setLinkUrl(qywxPushList.getLinkUrl());
            flag=false;
        }
        if(flag)
        {
            qywxActivityPushMapper.updatePushList(qywxPushList.getPushId(),"F","","","????????????");
        }else {
            //??????????????????????????????ID??????
            List<String> contactIdList=qywxManualDetailList.stream().map(QywxManualDetail::getQywxContactId).collect(Collectors.toList());
            log.info("???????????????????????????{}??????{}??????{}???",qywxMessage.toString(),qywxPushList.getFollowUserId(),contactIdList);
            //???????????????????????????????????????
            String result = qywxMessageService.pushQywxMessage(qywxMessage, qywxPushList.getFollowUserId(), contactIdList);
            log.info("??????????????????????????????????????????{}???", result);

            String status="S";
            String msgId ="";
            String failList="";
            String remark="????????????";

            if(StringUtils.isEmpty(result))
            {
                status="F";
                remark="?????????????????????????????????";
            }else{
                JSONObject jsonObject = JSON.parseObject(result);
                msgId = jsonObject.getString("msgid");
                int errcode = jsonObject.getIntValue("errcode");
                failList = jsonObject.getString("fail_list");

                if(errcode!=0){
                    status="F";
                    remark="??????????????????????????????";
                }
            }
            //???????????????
            qywxManualHeaderMapper.updatePushList(qywxPushList.getPushId(),status,msgId,failList,remark);

            List<Long> detailIdList=qywxManualDetailList.stream().map(QywxManualDetail::getDetailId).collect(Collectors.toList());
            log.info("????????????????????????????????????:???{}?????????{}?????????{}???",detailIdList.toString(),qywxPushList.getPushId(),msgId);
            if(detailIdList.size()>0)
            {
                qywxManualHeaderMapper.updatePushId(detailIdList,qywxPushList.getPushId(),msgId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QywxManualError saveManualData(MultipartFile file, QywxManualHeader qywxManualHeader) throws LinkSteadyException {
        // ??????file
        List<String[]> gbk =null;
        //???????????? ?????????????????????ID
        Set<String> followerSet= Sets.newHashSet();
        File tmpFile =null;
        try {
             tmpFile = FileUtils.multipartFileToFile(file);
            if(tmpFile == null) {
                throw new RuntimeException("????????????????????????");
            }
            gbk =getCsvData(file.getInputStream(), "gbk");
        }catch (Exception e) {
            throw new LinkSteadyException("?????????????????????");
        }
        //???????????????????????????
        List<QywxManualDetail> list=Lists.newArrayList();
        //??????????????????
        for (int i = 0; i < gbk.size(); i++) {
            String followerId="";
            if(gbk.get(i)[0].isEmpty()||gbk.get(i)[1].isEmpty())
            {
               throw new LinkSteadyException("?????????????????????ID?????????ID???????????????");
            }

            QywxManualDetail qywxManualDetail=new QywxManualDetail();
            if(!gbk.get(i)[0].isEmpty()){
                followerId=gbk.get(i)[0];
                if(!followerSet.contains(followerId)){
                    followerSet.add(followerId);
                }
                qywxManualDetail.setFollowerUserId(followerId);
            }

          if(!gbk.get(i)[1].isEmpty()){
              String externalUserId=gbk.get(i)[1];
              qywxManualDetail.setQywxContactId(externalUserId);
          }

          qywxManualDetail.setExecStatus(-999);
          qywxManualDetail.setInsertBy(((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername());
          qywxManualDetail.setInsertDt(new Date());
          list.add(qywxManualDetail);
        }
        //???????????????followuser?????????
        int userNumber = followerSet.size();//?????????
        int totalNum =gbk.size();//????????????????????????
        qywxManualHeader.setUserNumber(userNumber);
        qywxManualHeader.setTotalNum(totalNum);
        //??????????????????
        qywxManualHeaderMapper.saveQywxManualHeader(qywxManualHeader);
        long headId = qywxManualHeader.getHeadId();

        for (QywxManualDetail qywxManualDetail : list) {
            qywxManualDetail.setHeadId(headId);
        }
        //?????????????????????
        qywxManualHeaderMapper.saveQywxManualDetail(list);
        //????????????   userNumber,totalNum??????????????????????????????????????????????????????????????????????????????detail?????????????????????????????????
        QywxManualError qywxManualError = checkQywxManualDetail(headId,userNumber,totalNum);
        FileUtils.deleteTempFile(tmpFile);

        //?????????????????????????????????
        qywxManualHeaderMapper.updateQywxUserStatus(qywxManualHeader.getHeadId());
        return qywxManualError;
    }

    /**
     * ????????????
     * @param headId
     * @return
     */
    private QywxManualError checkQywxManualDetail(long headId,int userNumber,int totalNum){
        QywxManualError qywxManualError=new QywxManualError();
        String errorFlag="Y";
        String desc="????????????";
        List<QywxManualDetail> notExistsContactList = qywxManualHeaderMapper.getNotExistsContact(headId);
        if(notExistsContactList.size()>0){
            errorFlag="N";
            //??????????????????
            notExistsContactList=notExistsContactList.stream().distinct().collect(Collectors.toList());
            qywxManualHeaderMapper.updateTotalNum(totalNum-(notExistsContactList.size()),headId);
            for (QywxManualDetail detail : notExistsContactList) {
                qywxManualHeaderMapper.delContact(detail);
            }
            desc+="??????"+notExistsContactList.size()+"??????????????????????????????";
        }
        //???????????????
        qywxManualHeaderMapper.updateUserNumber(headId);

        qywxManualError.setErrorDesc(desc);
        qywxManualError.setErrorFlag(errorFlag);
        return  qywxManualError;
    }


    //??????csv????????????
    public static List<String[]> getCsvData(InputStream in, String charsetName) throws LinkSteadyException {
        List<String[]> list = new ArrayList<String[]>();
        int i = 0;
        try (CSVReader csvReader = new CSVReaderBuilder(
                new BufferedReader(new InputStreamReader(in,charsetName))).build()) {
            Iterator<String[]> iterator = csvReader.iterator();
            while (iterator.hasNext()) {
                String[] next = iterator.next();
                //?????????????????????????????????????????????
                if(i >= 1) {
                    list.add(next);
                }
                i++;
            }
            return list;
        } catch (Exception e) {
            log.error("CSV??????????????????,???{}???",e);
            throw new LinkSteadyException("?????????????????????");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteData(Long headId) {
        qywxManualHeaderMapper.deleteManualHeaderData(headId);
        qywxManualHeaderMapper.deleteManualDetail(headId);
    }

    @Override
    public QywxManualHeader getManualHeader(Long headId) {
        return qywxManualHeaderMapper.getQywxManualHeader(headId);
    }

    @Override
    public Map<String,Object> getHeaderEffectInfo(Long headId,String status) {
        return qywxManualHeaderMapper.getHeaderEffectInfo(headId,status);
    }
}
