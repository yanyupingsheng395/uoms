package com.linksteady.operate.service.impl;

import com.google.common.collect.Lists;
import com.linksteady.common.service.ConfigService;
import com.linksteady.operate.dao.QywxContactWayMapper;
import com.linksteady.operate.domain.QywxContactWay;
import com.linksteady.operate.service.QywxContactWayService;
import com.linksteady.operate.service.ShortUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author huang
 * @date 2020/7/17
 * 渠道活码的服务类
 */
@Service
@Slf4j
public class QywxContactWayServiceImpl implements QywxContactWayService {

    /**
     * 增加渠道活码接口地址
     */
    private static final String ADD_CONTACT_WAY="/api/addContactWay";
    /**
     * 更新渠道活码的接口地址
     */
    private static final String UPDATE_CONTACT_WAY="/api/updateContactWay";
    /**
     * 删除渠道活码的接口地址
     */
    private static final String DELETE_CONTACT_WAY="/api/deleteContactWay";
    /**
     * 获取渠道活码的详细信息
     */
    private static final String GET_CONTACT_WAY="/api/getContactWay";

    /**
     * 渠道活码展示页的详细地址
     */
    private static final String CONTACT_DETAIL_URL="/images/qrcode";


    @Autowired
    ConfigService configService;

    @Autowired
    QywxContactWayMapper qywxContactWayMapper;

    @Autowired
    ShortUrlService shortUrlService;

    @Autowired
  //  private QywxDeptAndUserMapper qywxDeptAndUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveContactWay(QywxContactWay qywxContactWay, String userName) throws Exception{
//        //渠道码类型 单人 or 多人
//        String usersListStr = qywxContactWay.getUsersList();
//        String deptListStr = qywxContactWay.getDeptList();
//        HashSet<String> userList = new HashSet<>();
//        if(StringUtils.isNotEmpty(usersListStr)) {
//            userList = new HashSet<>(Arrays.asList(usersListStr.split(",")));
//        }
//        if(StringUtils.isNotEmpty(deptListStr)) {
//            List<String> deptUserIdList = qywxDeptAndUserMapper.getUserIdsByDeptId(Arrays.asList(deptListStr.split(",")));
//            userList.addAll(deptUserIdList);
//        }
//        if(userList.size() > 1) {
//            qywxContactWay.setContactType("2");
//        }else if(userList.size() == 1){
//            qywxContactWay.setContactType("1");
//        }
//
//        //固定值为2 表示生成二维码
//        qywxContactWay.setScene("2");
//        //外部客户添加时是否无需验证，默认为true
//        qywxContactWay.setSkipVerify(true);
//
//        qywxContactWay.setCreateDt(new Date());
//        qywxContactWay.setUpdateDt(new Date());
//        qywxContactWay.setCreateBy(userName);
//        qywxContactWay.setUpdateBy(userName);
//
//        //保存渠道活码
//        qywxContactWayMapper.saveContactWay(qywxContactWay);
//        Long contactWayId=qywxContactWay.getContactWayId();
//        String url = configService.getValueByName(ConfigEnum.qywxDomainUrl.getKeyCode()) + ADD_CONTACT_WAY;
//
//        qywxContactWay.setUsersList(StringUtils.join(userList, ","));
//        String param = JSON.toJSONString(qywxContactWay);
//        String corpId=configService.getValueByName(ConfigEnum.qywxCorpId.getKeyCode());
//        //时间戳
//        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
//        String signature= SHA1.gen(timestamp,param);
//        String requesturl=url+"?corpId="+corpId +"&timestamp="+timestamp+"&signature="+signature;
//        //构造数据，请求企业微信端 生成渠道码
//        String result = OkHttpUtil.postRequestByJson(requesturl, param);
//        log.debug("请求获取渠道活码的url:{},参数:{},返回的结果{}", requesturl, param, result);
//
//        JSONObject jsonObject = JSON.parseObject(result);
//        if (null != jsonObject && jsonObject.getIntValue("code")==200) {
//            //返回的结构里面data的内容时configId
//            String configId = jsonObject.getString("data");
//            qywxContactWay.setConfigId(configId);
//
//            JSONObject contactDetail=JSON.parseObject(getContactWayByConfigId(qywxContactWay.getConfigId()));
//            //更新configId获取一次二维码的地址
//            String qrCode="";
//            if(null!=contactDetail&&StringUtils.isNotEmpty(contactDetail.getString("qr_code")))
//            {
//                qrCode=contactDetail.getString("qr_code");
//                qywxContactWay.setQrCode(qrCode);
//            }
//            String shortUrl="";
//            qywxContactWayMapper.updateContactWayFullInfo(contactWayId,configId,qrCode,shortUrl,qywxContactWay.getUpdateBy());
//        }else
//        {
//            throw new LinkSteadyException("新增渠道活码失败！");
//        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContractWay(QywxContactWay qywxContactWay) throws Exception{
//        //更新数据库
//        qywxContactWayMapper.updateContractWay(qywxContactWay);
//        String updateUrl = configService.getValueByName(ConfigEnum.qywxDomainUrl.getKeyCode()) + UPDATE_CONTACT_WAY;
//        String param = JSON.toJSONString(qywxContactWay);
//
//        String corpId=configService.getValueByName(ConfigEnum.qywxCorpId.getKeyCode());
//        //时间戳
//        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
//        String signature= SHA1.gen(timestamp,param);
//        String requesturl=updateUrl+"?corpId="+corpId +"&timestamp="+timestamp+"&signature="+signature;
//
//        //构造数据，请求企业微信端 生成渠道码
//        String result = OkHttpUtil.postRequestByJson(requesturl, param);
//        log.debug("请求更新渠道活码的url:{},参数:{},返回的结果{}", requesturl, param, result);
//
//        JSONObject jsonObject = JSON.parseObject(result);
//        if (null != jsonObject && jsonObject.getIntValue("code")==200) {
//            //如果调用企业微信端接口成功，则重新获取一遍数据
//            JSONObject contactDetail=JSON.parseObject(getContactWayByConfigId(qywxContactWay.getConfigId()));
//            String qrCode="";
//            if(null!=contactDetail&&StringUtils.isNotEmpty(contactDetail.getString("qr_code")))
//            {
//                qrCode=contactDetail.getString("qr_code");
//            }
//            qywxContactWayMapper.updateContactWayQrCode(qywxContactWay.getContactWayId(),qrCode,qywxContactWay.getUpdateBy());
//        }else
//        {
//            throw new LinkSteadyException("更新失败！");
//        }
    }

    @Override
    public int getContactWayCount(String qstate) {
        return qywxContactWayMapper.getContactWayCount(qstate);
    }

    @Override
    public List<QywxContactWay> getContactWayList(int limit,int offset,String qstate) {
//        String corpId = configService.getValueByName(ConfigEnum.qywxCorpId.getKeyCode());
//        List<QywxContactWay> contactWayList = qywxContactWayMapper.getContactWayList(limit, offset, qstate);
//        List<QywxDeptAndUser> deptAndUserData = qywxDeptAndUserMapper.getDeptAndUserData(corpId);
//        Map<String, String> userMap = deptAndUserData.stream().filter(x->StringUtils.isNotEmpty(x.getUserId()) && StringUtils.isNotEmpty(x.getUserName())).collect(Collectors.toMap(QywxDeptAndUser::getUserId, QywxDeptAndUser::getUserName, BinaryOperator.minBy(Comparator.naturalOrder())));
//        Map<String, String> deptMap = deptAndUserData.stream().filter(x->StringUtils.isNotEmpty(x.getDeptId()) && StringUtils.isNotEmpty(x.getDeptName())).collect(Collectors.toMap(QywxDeptAndUser::getDeptId, QywxDeptAndUser::getDeptName, BinaryOperator.minBy(Comparator.naturalOrder())));
//        contactWayList.stream().forEach(x->{
//            String userIds = x.getUsersList();
//            String deptIds = x.getDeptList();
//            if(StringUtils.isNotEmpty(userIds)) {
//                List<String> userIdList = Arrays.asList(userIds.split(","));
//                List<String> userNameList = userIdList.stream().map(k->userMap.get(k)).collect(Collectors.toList());
//                x.setUsersList(StringUtils.join(userNameList, ","));
//            }
//            if(StringUtils.isNotEmpty(deptIds)) {
//                List<String> deptIdList = Arrays.asList(deptIds.split(","));
//                List<String> deptNameList = deptIdList.stream().map(k->deptMap.get(k)).collect(Collectors.toList());
//                x.setDeptList(StringUtils.join(deptNameList, ","));
//            }
//        });
//        return contactWayList;

        List<QywxContactWay> list= Lists.newArrayList();
        return list;
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
    public String getContactWayByConfigId(String configId) {
//        String getUrl = configService.getValueByName(ConfigEnum.qywxDomainUrl.getKeyCode()) + GET_CONTACT_WAY;
//
//        String corpId=configService.getValueByName(ConfigEnum.qywxCorpId.getKeyCode());
//        //时间戳
//        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
//        String signature= SHA1.gen(timestamp,configId);
//        String requesturl=getUrl+"?corpId="+corpId +"&timestamp="+timestamp+"&signature="+signature;
//
//        //构造数据，请求企业微信端 生成渠道码
//        String result = OkHttpUtil.postRequest(requesturl, configId);
//        log.debug("请求获取渠道活码详细信息的url:{},参数:{},返回的结果{}", requesturl, configId, result);
//
//        JSONObject jsonObject = JSON.parseObject(result);
//        if (null != jsonObject && jsonObject.getIntValue("code")==200)
//        {
//            return jsonObject.getString("data");
//        }else
//        {
//            return "";
//        }

        return "";
    }

    @Override
    public void updateShortUrl(Long contactWayId, String shortUrl,String updateBy) {
        qywxContactWayMapper.updateShortUrl(contactWayId,shortUrl,updateBy);
    }

    @Override
    @Transactional
    public void deleteContactWay(String configId) throws Exception{
//        log.info("删除渠道活码，接收到的configId为{}",configId);
//        if(qywxContactWayMapper.getRefrenceCount(configId)>0)
//        {
//            throw new Exception("渠道活码已被拉新任务引用，无法删除!");
//        }
//
//        //发送到企业微信端进行删除
//        String delUrl = configService.getValueByName(ConfigEnum.qywxDomainUrl.getKeyCode()) + DELETE_CONTACT_WAY;
//
//        String corpId=configService.getValueByName(ConfigEnum.qywxCorpId.getKeyCode());
//        //时间戳
//        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
//        String signature= SHA1.gen(timestamp,configId);
//        String requesturl=delUrl+"?corpId="+corpId +"&timestamp="+timestamp+"&signature="+signature;
//
//        //构造数据，请求企业微信端 生成渠道码
//        String result = OkHttpUtil.postRequest(requesturl, configId);
//        log.info("请求删除渠道活码的url:{},参数:{},返回的结果{}", requesturl, configId, result);
//
//        JSONObject jsonObject = JSON.parseObject(result);
//        if (null != jsonObject && jsonObject.getIntValue("code")==200)
//        {
//            //从数据库进行删除
//            qywxContactWayMapper.deleteContactWay(configId);
//        }else
//        {
//            throw new LinkSteadyException("删除失败！");
//        }
    }

    @Override
    public QywxContactWay getQrcodeByConfigId(String configId) {
        return qywxContactWayMapper.getQrcodeByConfigId(configId);
    }
}
