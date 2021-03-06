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
        //更新状态为2，防止记录被其他用户更改        2为进行中的状态，0表示初始状态
        int count=qywxManualHeaderMapper.updateStatusToPlaning(headId,"2","0",null);
        if(count==0){
            throw new OptimisticLockException();
        }
        //获取推送详细表信息
        List<QywxManualDetail> detail=qywxManualHeaderMapper.getQywxManualDetail(headId);
        //获取该headiD下所有的followuser的集合
        List<String> followerUserIdList = detail.stream().map(QywxManualDetail::getFollowerUserId).distinct().collect(Collectors.toList());
        String appId = qywxMessageService.getMpAppId();
        followerUserIdList.stream().forEach(x->{
            String followerUserId = x;
            //获得当前的总记录数
            int countDetail = qywxManualHeaderMapper.getQywxManualDetailCount(headId, followerUserId);
            int pageSize = 10000;
            //微信推送一次只能推送最多一万条
            if(countDetail<=pageSize){
                log.info("当前推送数据量<=10000");
                if(countDetail>0){
                    //查询具体的多少条
                    List<QywxManualDetail> qywxManualDetailList = qywxManualHeaderMapper.getQywxManualDetailByPage(headId, followerUserId, 0, 10000);
                    //组织推送消息
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
                    //推送并更新状态
                    pushQywxMsg(qywxPushList,qywxManualDetailList,header);
                }
            }else{
                int pageNum = countDetail % pageSize == 0 ? (countDetail / pageSize) : ((countDetail / pageSize) + 1);
                for (int i = 0; i < pageNum; i++) {
                    log.info("当前文本推送条数{}，偏移量为{}", pageSize,i*pageSize);
                    //获取活动明细表，条数大于一万条，然后分页查询推送
                    List<QywxManualDetail> qywxManualDetailList = qywxManualHeaderMapper.getQywxManualDetailByPage(headId, followerUserId, i * pageSize, pageSize);
                    if(qywxManualDetailList.size() > 0) {
                        //组织推送信息
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
                        //将信息，放入uo_qywx_push_list表中。
                        qywxManualHeaderMapper.insertPushList(qywxPushList);
                        //推送并更新状态
                        pushQywxMsg(qywxPushList,qywxManualDetailList,header);
                    }
                }
            }
        });
        LocalDateTime pushtime = LocalDateTime.now();
        //更新头表状态为"已推送"
       int failCount=qywxManualHeaderMapper.getPushDetailStatus(headId);
       log.info("推送消息失败数量{}",failCount);
       String status="1";
       if(failCount>0){
           status="3";
       }
        qywxManualHeaderMapper.updateStatusToPlaning(headId,status,"2",pushtime);
    }

    /**
     * 推送企业微信消息
     *
     * @param qywxPushList (待推送的对象)
     */
    public void pushQywxMsg(QywxPushList qywxPushList, List<QywxManualDetail> qywxManualDetailList,QywxManualHeader header) {
        log.info("推送集合{}",qywxPushList.toString());
        log.info("头表数据{}",header.toString());
        if(null==qywxManualDetailList||qywxManualDetailList.size()==0){
            qywxActivityPushMapper.updatePushList(qywxPushList.getPushId(),"F","","","推送列表为空");
            return;
        }
        //判断文本或小程序至少有一个的变量
        boolean flag=true;
        QywxMessage qywxMessage = new QywxMessage();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(qywxPushList.getTextContent())) {
            qywxMessage.setText(qywxPushList.getTextContent());
            flag=false;
        }
        //小程序
        if("applets".equals(header.getMsgType()))
        {
            qywxMessage.setMpTitle(qywxPushList.getMpTitle());
            qywxMessage.setMpPicMediaId(qywxPushList.getMpMediaId());
            qywxMessage.setMpAppid(qywxPushList.getMpAppid());
            qywxMessage.setMpPage(qywxPushList.getMpUrl());
            flag=false;
        }
        //图片
        if("image".equals(header.getMsgType())){
            qywxMessage.setImgPicUrl(qywxPushList.getPicUrl());
            flag=false;
        }
        //网页
        if("web".equals(header.getMsgType())){
            qywxMessage.setLinkTitle(qywxPushList.getLinkTitle());
            qywxMessage.setLinkPicUrl(qywxPushList.getLinkPicurl());
            qywxMessage.setLinkDesc(qywxPushList.getLinkDesc());
            qywxMessage.setLinkUrl(qywxPushList.getLinkUrl());
            flag=false;
        }
        if(flag)
        {
            qywxActivityPushMapper.updatePushList(qywxPushList.getPushId(),"F","","","消息为空");
        }else {
            //获取对应企业微信客户ID集合
            List<String> contactIdList=qywxManualDetailList.stream().map(QywxManualDetail::getQywxContactId).collect(Collectors.toList());
            log.info("推送微信消息参数【{}】【{}】【{}】",qywxMessage.toString(),qywxPushList.getFollowUserId(),contactIdList);
            //调用企业微信接口，发送信息
            String result = qywxMessageService.pushQywxMessage(qywxMessage, qywxPushList.getFollowUserId(), contactIdList);
            log.info("手动推送企微消息：推送结果【{}】", result);

            String status="S";
            String msgId ="";
            String failList="";
            String remark="推送成功";

            if(StringUtils.isEmpty(result))
            {
                status="F";
                remark="调用企业微信接口返回空";
            }else{
                JSONObject jsonObject = JSON.parseObject(result);
                msgId = jsonObject.getString("msgid");
                int errcode = jsonObject.getIntValue("errcode");
                failList = jsonObject.getString("fail_list");

                if(errcode!=0){
                    status="F";
                    remark="调用企业微信接口失败";
                }
            }
            //更新状态。
            qywxManualHeaderMapper.updatePushList(qywxPushList.getPushId(),status,msgId,failList,remark);

            List<Long> detailIdList=qywxManualDetailList.stream().map(QywxManualDetail::getDetailId).collect(Collectors.toList());
            log.info("回写手动推送任务表的参数:【{}】，【{}】，【{}】",detailIdList.toString(),qywxPushList.getPushId(),msgId);
            if(detailIdList.size()>0)
            {
                qywxManualHeaderMapper.updatePushId(detailIdList,qywxPushList.getPushId(),msgId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QywxManualError saveManualData(MultipartFile file, QywxManualHeader qywxManualHeader) throws LinkSteadyException {
        // 解析file
        List<String[]> gbk =null;
        //定义集合 存放所有的成员ID
        Set<String> followerSet= Sets.newHashSet();
        File tmpFile =null;
        try {
             tmpFile = FileUtils.multipartFileToFile(file);
            if(tmpFile == null) {
                throw new RuntimeException("上传的文件为空！");
            }
            gbk =getCsvData(file.getInputStream(), "gbk");
        }catch (Exception e) {
            throw new LinkSteadyException("文件解析异常！");
        }
        //手动推送明细表集合
        List<QywxManualDetail> list=Lists.newArrayList();
        //循环存入数据
        for (int i = 0; i < gbk.size(); i++) {
            String followerId="";
            if(gbk.get(i)[0].isEmpty()||gbk.get(i)[1].isEmpty())
            {
               throw new LinkSteadyException("上传文件中成员ID和客户ID不能为空！");
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
        //文件中包含followuser的人数
        int userNumber = followerSet.size();//用户数
        int totalNum =gbk.size();//需要推送的成员数
        qywxManualHeader.setUserNumber(userNumber);
        qywxManualHeader.setTotalNum(totalNum);
        //插入头表数据
        qywxManualHeaderMapper.saveQywxManualHeader(qywxManualHeader);
        long headId = qywxManualHeader.getHeadId();

        for (QywxManualDetail qywxManualDetail : list) {
            qywxManualDetail.setHeadId(headId);
        }
        //插入明细表数据
        qywxManualHeaderMapper.saveQywxManualDetail(list);
        //验证数据   userNumber,totalNum是成员数和需要推送人数。如果出现验证身份不通过，删除detail表数据，更新头表的数量
        QywxManualError qywxManualError = checkQywxManualDetail(headId,userNumber,totalNum);
        FileUtils.deleteTempFile(tmpFile);

        //更新是否匹配商城的标记
        qywxManualHeaderMapper.updateQywxUserStatus(qywxManualHeader.getHeadId());
        return qywxManualError;
    }

    /**
     * 验证数据
     * @param headId
     * @return
     */
    private QywxManualError checkQywxManualDetail(long headId,int userNumber,int totalNum){
        QywxManualError qywxManualError=new QywxManualError();
        String errorFlag="Y";
        String desc="保存成功";
        List<QywxManualDetail> notExistsContactList = qywxManualHeaderMapper.getNotExistsContact(headId);
        if(notExistsContactList.size()>0){
            errorFlag="N";
            //去除重复数据
            notExistsContactList=notExistsContactList.stream().distinct().collect(Collectors.toList());
            qywxManualHeaderMapper.updateTotalNum(totalNum-(notExistsContactList.size()),headId);
            for (QywxManualDetail detail : notExistsContactList) {
                qywxManualHeaderMapper.delContact(detail);
            }
            desc+="其中"+notExistsContactList.size()+"人非企微客户，已删除";
        }
        //更新用户数
        qywxManualHeaderMapper.updateUserNumber(headId);

        qywxManualError.setErrorDesc(desc);
        qywxManualError.setErrorFlag(errorFlag);
        return  qywxManualError;
    }


    //解析csv文件内容
    public static List<String[]> getCsvData(InputStream in, String charsetName) throws LinkSteadyException {
        List<String[]> list = new ArrayList<String[]>();
        int i = 0;
        try (CSVReader csvReader = new CSVReaderBuilder(
                new BufferedReader(new InputStreamReader(in,charsetName))).build()) {
            Iterator<String[]> iterator = csvReader.iterator();
            while (iterator.hasNext()) {
                String[] next = iterator.next();
                //去除第一行的表头，从第二行开始
                if(i >= 1) {
                    list.add(next);
                }
                i++;
            }
            return list;
        } catch (Exception e) {
            log.error("CSV文件读取异常,【{}】",e);
            throw new LinkSteadyException("文件解析异常！");
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
