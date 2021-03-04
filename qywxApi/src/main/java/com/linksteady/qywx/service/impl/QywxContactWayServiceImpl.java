package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.linksteady.common.service.ConfigService;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.QywxContactWayMapper;
import com.linksteady.qywx.domain.QywxContactWay;
import com.linksteady.qywx.domain.QywxContactWayDetail;
import com.linksteady.qywx.service.QywxContactWayService;
import com.linksteady.qywx.service.QywxService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huang
 * @date 2020/7/17
 * 渠道活码的服务类
 */
@Service
@Slf4j
public class QywxContactWayServiceImpl implements QywxContactWayService {

    @Autowired
    ConfigService configService;

    @Autowired(required = false)
    QywxContactWayMapper qywxContactWayMapper;

    @Autowired
    QywxService qywxService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveContactWay(QywxContactWay qywxContactWay, String userName) throws Exception{
        //渠道码类型 单人 or 多人
        String usersListStr = qywxContactWay.getUsersList();//选择人
        String deptListStr = qywxContactWay.getDeptList();//选择部门
        HashSet<String> userList = new HashSet<>();
        if(StringUtils.isNotEmpty(usersListStr)) {
            userList = new HashSet<>(Arrays.asList(usersListStr.split(",")));
        }
        HashSet<String> deptList = new HashSet<>();
        if(StringUtils.isNotEmpty(deptListStr)) {
            deptList = new HashSet<>(Arrays.asList(deptListStr.split(",")));
        }else{
            qywxContactWay.setDeptList(null);
        }

        if(userList.size() > 1||deptList.size()>0) {
            qywxContactWay.setContactType("2");
        }else if(userList.size() == 1){
            qywxContactWay.setContactType("1");
        }

        //固定值为2 表示生成二维码
        qywxContactWay.setScene("2");
        //外部客户添加时是否无需验证，默认为true
        qywxContactWay.setSkipVerify(true);

        qywxContactWay.setCreateDt(new Date());
        qywxContactWay.setUpdateDt(new Date());
        qywxContactWay.setCreateBy(userName);
        qywxContactWay.setUpdateBy(userName);

        //保存渠道活码
         qywxContactWayMapper.saveContactWay(qywxContactWay);
        Long contactWayId=qywxContactWay.getContactWayId();
        insertContactWayDetail(deptListStr,usersListStr,contactWayId);
        JSONObject params=new JSONObject();
        params.put("type",qywxContactWay.getContactType());
        params.put("scene",qywxContactWay.getScene());
        params.put("skip_verify",qywxContactWay.getSkipVerify());
        params.put("state",qywxContactWay.getState());
        List userLists= Splitter.on(',').trimResults().omitEmptyStrings().splitToList(qywxContactWay.getUsersList());
        if(userLists.size()>0){
            params.put("user",JSON.parseArray(JSON.toJSONString(userLists)));
        }
        if(StringUtils.isNotEmpty(deptListStr)){//如果部门选择不为null
            String[] split1 = deptListStr.split(",");
            JSONArray jsonArray=new JSONArray();
            for (int i = 0; i <split1.length; i++) {
                jsonArray.add(Integer.parseInt(split1[i]));
            }
            params.put("party",jsonArray);
        }
        log.info("渠道活码发送的参数为{}",JSON.toJSONString(params));
        String url= qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.ADD_CONTACT_WAY)+qywxService.getAccessToken();
        String result=OkHttpUtil.postRequest(url,JSON.toJSONString(params));

        JSONObject jsonObject = JSON.parseObject(result);
        if (null != jsonObject && jsonObject.getIntValue("errcode")==0) {
            //返回的结构里面data的内容时configId
            String configId = jsonObject.getString("config_id");
            qywxContactWay.setConfigId(configId);

            JSONObject contactDetail=JSON.parseObject(getContactWayByConfigId(qywxContactWay.getConfigId()));
            //更新configId获取一次二维码的地址
            String qrCode="";
            if(null!=contactDetail&&StringUtils.isNotEmpty(contactDetail.getString("qr_code")))
            {
                qrCode=contactDetail.getString("qr_code");
                qywxContactWay.setQrCode(qrCode);
            }
            String shortUrl="";
            qywxContactWayMapper.updateContactWayFullInfo(contactWayId,configId,qrCode,shortUrl,qywxContactWay.getUpdateBy());

        }else{
            throw new Exception("新增渠道活码失败！");
        }
    }

    private void insertContactWayDetail(String deptListStr,String usersListStr,Long contactWayId){
        List<QywxContactWayDetail> alllist=new ArrayList<>();
        if(StringUtils.isNotEmpty(deptListStr)){
            List<String> strings = Arrays.asList(deptListStr.split(","));
            List<Long> longs = strings.stream().map(x -> Long.parseLong(x)).collect(Collectors.toList());
            List<QywxContactWayDetail> deptList1 = qywxContactWayMapper.getDeptList(contactWayId, longs);
            if(deptList1.size()>0){
                deptList1.stream().forEach(x->x.setInsertDt(new Date()));
                alllist.addAll(deptList1);
            }
        }
        if(StringUtils.isNotEmpty(usersListStr)){
            List<QywxContactWayDetail> userListDetail= qywxContactWayMapper.getUserList(contactWayId ,Arrays.asList(usersListStr.split(",")));
            if(userListDetail.size()>0){
                userListDetail.stream().forEach(x->x.setInsertDt(new Date()));
                alllist.addAll(userListDetail);
            }
        }
        if(alllist.size()>0){
            qywxContactWayMapper.insertContactWayDetail(alllist);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContractWay(QywxContactWay qywxContactWay) throws Exception{
        JSONObject param=new JSONObject();
        param.put("type",qywxContactWay.getContactType());
        param.put("scene",qywxContactWay.getScene());
        param.put("skip_verify",qywxContactWay.getSkipVerify());
        param.put("state",qywxContactWay.getState());
        String configId=qywxContactWay.getConfigId();
        if(StringUtils.isEmpty(configId))
        {
            throw new Exception("configId不能为空");
        }
        param.put("config_id",configId);

        List userList= Splitter.on(',').trimResults().omitEmptyStrings().splitToList(qywxContactWay.getUsersList());//获取人员列表
        List deptlist = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(qywxContactWay.getDeptList());//获取部门列表
        if(userList.size()>0){
            param.put("user",JSON.parseArray(JSON.toJSONString(userList)));
        }
        if(deptlist.size()>0){
            param.put("party",JSON.parseArray(JSON.toJSONString(deptlist)));
        }else{
            qywxContactWay.setDeptList(null);
        }


        log.info("渠道活码发送的参数为{}",JSON.toJSONString(param));
        String url= qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.UPDATE_CONTACT_WAY)+qywxService.getAccessToken();
        String result=OkHttpUtil.postRequest(url,param.toJSONString());
        log.info("{}新增渠道活码返回的结果为{}",qywxService.getCorpId(),result);

        JSONObject jsonObject = JSON.parseObject(result);
        if (null != jsonObject && jsonObject.getIntValue("errcode")== 0) {
            //如果调用企业微信端接口成功，则重新获取一遍数据
            JSONObject contactDetail=JSON.parseObject(getContactWayByConfigId(qywxContactWay.getConfigId()));
            String qrCode="";
            if(null!=contactDetail&&StringUtils.isNotEmpty(contactDetail.getString("qr_code")))
            {
                qrCode=contactDetail.getString("qr_code");
            }

            qywxContactWayMapper.deleteContactWayDetail(qywxContactWay.getConfigId());
            insertContactWayDetail(qywxContactWay.getDeptList(),qywxContactWay.getUsersList(),qywxContactWay.getContactWayId());
            //更新数据库
            qywxContactWayMapper.updateContractWay(qywxContactWay);
            qywxContactWayMapper.updateContactWayQrCode(qywxContactWay.getContactWayId(),qrCode,qywxContactWay.getUpdateBy());
        }else
        {
            throw new Exception("更新失败！");
        }
    }

    @Override
    public int getContactWayCount(String qstate) {
        return qywxContactWayMapper.getContactWayCount(qstate);
    }

    @Override
    public List<QywxContactWay> getContactWayList(int limit,int offset,String qstate) {
        List<QywxContactWay> contactWayList = qywxContactWayMapper.getContactWayList(limit, offset, qstate);
        for (QywxContactWay qywxContactWay : contactWayList) {
            List<QywxContactWayDetail> contactWayDetail = qywxContactWayMapper.getContactWayDetail(qywxContactWay.getContactWayId());
            List<String> userlist = contactWayDetail.stream().filter(x -> "U".equals(x.getObjType())).map(QywxContactWayDetail::getObjName).collect(Collectors.toList());
            if(userlist.size()>0){
                qywxContactWay.setUsersList(StringUtils.join(userlist, ","));
            }
            List<String> deptNameList = contactWayDetail.stream().filter(x -> "D".equals(x.getObjType())).map(QywxContactWayDetail::getObjName).collect(Collectors.toList());
            if(deptNameList.size()>0){
                qywxContactWay.setDeptList(StringUtils.join(deptNameList, ","));
            }
        }
        return contactWayList;

    }

    @Override
    public int getContactWayValidUrlCount() {
        return qywxContactWayMapper.getContactWayValidUrlCount();
    }

    @Override
    public List<QywxContactWay> getContactWayValidUrlList(int limit,int offset) {
        return qywxContactWayMapper.getContactWayValidUrlList(limit,offset);
    }

    @Override
    public QywxContactWay getContactWayById(Long contactWayId) {
        return qywxContactWayMapper.getContactWayById(contactWayId);
    }

    /**
     * 根据configId从企业微信获取渠道活码的详细信息
     * @param configId
     * @return
     */
    @Override
    public String getContactWayByConfigId(String configId)  throws Exception{
        JSONObject param=new JSONObject();
        param.put("config_id",configId);
        String url= qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_CONTACT_WAY)+qywxService.getAccessToken();
        log.info("获取渠道活码请求的url为{},参数为{}",url,JSON.toJSONString(param));
        String result=OkHttpUtil.postRequestByJson(url,param.toJSONString());
        log.info("{}新增渠道活码返回的结果为{}",qywxService.getCorpId(),result);

        JSONObject jsonObject = JSON.parseObject(result);
        if (null != jsonObject && jsonObject.getIntValue("errcode")== 0)
        {
            return jsonObject.getString("contact_way");
        }else
        {
            return "";
        }
    }

    @Override
    public void updateShortUrl(Long contactWayId, String shortUrl,String updateBy) {
        qywxContactWayMapper.updateShortUrl(contactWayId,shortUrl,updateBy);
    }

    @Override
    @Transactional
    public void deleteContactWay(String configId,Long contactWayId) throws Exception{
        log.info("删除渠道活码，接收到的configId为{}",configId);
        if(qywxContactWayMapper.getRefrenceCount(configId)>0)
        {
            throw new Exception("渠道活码已被拉新任务引用，无法删除!");
        }
        //构造企业微信需要的参数
        JSONObject param=new JSONObject();
        if(StringUtils.isEmpty(configId))
        {
            throw new Exception("configId不能为空");
        }
        param.put("config_id",configId);

        String url= qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.DEL_CONTACT_WAY)+qywxService.getAccessToken();
        String result=OkHttpUtil.postRequestByJson(url,param.toJSONString());


        JSONObject jsonObject = JSON.parseObject(result);
        if (null != jsonObject && jsonObject.getIntValue("errcode")== 0)
        {
            qywxContactWayMapper.deleteContactWayDetail(configId);
            //从数据库进行删除
            qywxContactWayMapper.deleteContactWay(configId);

        }else
        {
            throw new Exception("删除失败！");
        }
    }

    @Override
    public QywxContactWay getQrcodeByConfigId(String configId) {
        return qywxContactWayMapper.getQrcodeByConfigId(configId);
    }

}
