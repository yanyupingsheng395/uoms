package com.linksteady.operate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
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
        int count=qywxManualHeaderMapper.updateStatusToPlaning(headId,"2","0");
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
                    qywxPushList.setExternalContactIds(org.apache.commons.lang3.StringUtils.join( qywxManualDetailList.stream().map(QywxManualDetail::getQywxContactId).collect(Collectors.toList()),","));
                    qywxPushList.setFollowUserId(followerUserId);
                    qywxPushList.setSourceId(detail.get(0).getDetailId());
                    qywxManualHeaderMapper.insertPushList(qywxPushList);
                    //推送并更新状态
                    pushQywxMsg(qywxPushList,qywxManualDetailList);
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
                        qywxPushList.setExternalContactIds(org.apache.commons.lang3.StringUtils.join(qywxManualDetailList.stream().map(QywxManualDetail::getQywxContactId).collect(Collectors.toList()),","));
                        qywxPushList.setFollowUserId(followerUserId);
                        qywxPushList.setSourceId(detail.get(0).getDetailId());
                        //将信息，放入uo_qywx_push_list表中。
                        qywxManualHeaderMapper.insertPushList(qywxPushList);
                        //推送并更新状态
                        pushQywxMsg(qywxPushList,qywxManualDetailList);
                    }
                }
            }
        });
        //更新头表状态为"已推送"
        qywxManualHeaderMapper.updateStatusToPlaning(headId,"1","2");
    }

    /**
     * 推送企业微信消息
     *
     * @param qywxPushList (待推送的对象)
     */
    public void pushQywxMsg(QywxPushList qywxPushList, List<QywxManualDetail> qywxManualDetailList) {

        if(null==qywxManualDetailList||qywxManualDetailList.size()==0){
            qywxActivityPushMapper.updatePushList(qywxPushList.getPushId(),"F","","","推送列表为空");
            return;
        }

        String msgContent = qywxPushList.getTextContent();
        String mpTitle = qywxPushList.getMpTitle();
        String mpUrl = qywxPushList.getMpUrl();
        String mediaId =qywxPushList.getMpMediaId();
        String appId = qywxPushList.getMpAppid();

        //判断文本或小程序至少有一个的变量
        boolean flag=true;
        QywxMessage qywxMessage = new QywxMessage();

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(msgContent)) {
            qywxMessage.setText(msgContent);
            flag=false;
        }
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(mpTitle))
        {
            qywxMessage.setMpTitle(mpTitle);
            qywxMessage.setMpPicMediaId(mediaId);
            qywxMessage.setMpAppid(appId);
            qywxMessage.setMpPage(mpUrl);
            flag=false;
        }
        if(flag)
        {
            qywxActivityPushMapper.updatePushList(qywxPushList.getPushId(),"F","","","消息为空");
        }else {
            //获取对应企业微信客户ID集合
            List<String> contactIdList=qywxManualDetailList.stream().map(QywxManualDetail::getQywxContactId).collect(Collectors.toList());
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
            log.info("更新pushidpushid:{}",qywxPushList.getPushId());
            if(detailIdList.size()>0)
            {
                qywxManualHeaderMapper.updatePushId(detailIdList,qywxPushList.getPushId(),msgId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QywxManualError saveManualData(String smsContent, MultipartFile file, String mpTitle, String mpUrl, String mediaId) throws LinkSteadyException {
        QywxManualError qywxManualError=new QywxManualError();
        // 保存QywxManualHeader
        QywxManualHeader qywxManualHeader = new QywxManualHeader();
        qywxManualHeader.setInsertBy(((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername());
        qywxManualHeader.setInsertDt(new Date());
        qywxManualHeader.setStatus("0");
        qywxManualHeader.setTextContent(smsContent);
        qywxManualHeader.setMpTitle(mpTitle);
        qywxManualHeader.setMpMediald(mediaId);
        qywxManualHeader.setMpUrl(mpUrl);

        // 解析file
        List<String> followerIds = Lists.newArrayList();
        List<String> externalUserIds = Lists.newArrayList();
        List<String[]> gbk =new ArrayList<String[]>();
        //定义集合，校验过的followuerid放入集合中;
        List<String> checkfollower=Lists.newArrayList();
        try {
            File tmpFile = FileUtils.multipartFileToFile(file);
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
            QywxManualDetail qywxManualDetail=new QywxManualDetail();
            if(!gbk.get(i)[0].isEmpty()){
                followerId=gbk.get(i)[0];
                if(!checkfollower.contains(followerId)){
                    checkfollower.add(followerId);
                }
                followerIds.add(followerId);
                qywxManualDetail.setFollowerUserId(followerId);
            }
          if(!gbk.get(i)[1].isEmpty()){
              String externalUserId=gbk.get(i)[1];
              externalUserIds.add(externalUserId);
              qywxManualDetail.setQywxContactId(externalUserId);
          }
          qywxManualDetail.setExecStatus(-999);
          qywxManualDetail.setInsertBy(((UserBo) SecurityUtils.getSubject().getPrincipal()).getUsername());
          qywxManualDetail.setInsertDt(new Date());
          list.add(qywxManualDetail);
        }
        //文件中包含followuser的人数
        qywxManualHeader.setUserNumber(checkfollower.size());
        qywxManualHeader.setTotalNum(gbk.size());
        //插入头表数据
        qywxManualHeaderMapper.saveQywxManualHeader(qywxManualHeader);
        long headId = qywxManualHeader.getHeadId();

        for (QywxManualDetail qywxManualDetail : list) {
            qywxManualDetail.setHeadId(headId);
        }
        //插入明细表数据
        qywxManualHeaderMapper.saveQywxManualDetail(list);
        //验证数据
        qywxManualError = checkQywxManualDetail(headId, followerIds, externalUserIds);
        if("N".equals(qywxManualError.getErrorFlag())){
            throw new LinkSteadyException(qywxManualError.getErrorDesc());
        }else {
            qywxManualError.setErrorFlag("Y");
            qywxManualError.setErrorDesc("新增成功！");
            return qywxManualError;
        }
    }

    /**
     * 验证数据
     * @param headId
     * @param followerIds
     * @param externalUserIds
     * @return
     */
    private QywxManualError checkQywxManualDetail(long headId,List<String> followerIds, List<String> externalUserIds){
        QywxManualError qywxManualError=new QywxManualError();
        String errorFlag="Y";
        if(!(followerIds.size()==externalUserIds.size())){
            qywxManualError.setErrorFlag("N");
            qywxManualError.setErrorDesc("上传文件成员数和外部客户数量不匹配");
            return qywxManualError;
        }
        List<String> allFollwUserId = qywxManualHeaderMapper.getAllFollwUserId(headId);
        List<String> resultFollow = removeAll(followerIds, allFollwUserId);
        //followerIds，说明上传文件中存在有的followuserid不存在
       if(resultFollow.size()>0){
           errorFlag="N";
           qywxManualError.setErrorFollow(resultFollow);
           qywxManualError.setErrorDesc("文件中成员信息未查到或成员信息和外部客户信息不匹配！");
       }
        List<String> allExternal = qywxManualHeaderMapper.getAllContactId(headId);
        List<String> resultExternal = removeAll(externalUserIds, allExternal);
        if(resultExternal.size()>0){
            errorFlag="N";
            qywxManualError.setErrorExternal(resultExternal);
            qywxManualError.setErrorDesc("文件中成员信息未查到或成员信息和外部客户信息不匹配！");
        }
        qywxManualError.setErrorFlag(errorFlag);
        return  qywxManualError;
    }

    /**
     *list集合去除重复
     * @return
     */
    private List<String> removeAll(List<String> source, List<String> destination) {
        List<String> result = new LinkedList<String>();

        Map<String, Integer> sourceMap = new HashMap<String, Integer>();
        for (String t : source) {
            if (sourceMap.containsKey(t)) {
                sourceMap.put(t, sourceMap.get(t) + 1);
            } else {
                sourceMap.put(t, 1);
            }
        }

        Set<String> all = new HashSet<String>(destination);
        for (Map.Entry<String, Integer> entry : sourceMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (all.add(key)) {
                for (int i = 0; i < value; i++) {
                    result.add(key);
                }
            }
        }
        return result;
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
}
