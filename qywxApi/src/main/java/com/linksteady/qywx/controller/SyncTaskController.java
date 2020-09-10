package com.linksteady.qywx.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.linksteady.common.domain.QywxMessage;
import com.linksteady.common.domain.ResponseBo;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.common.util.crypto.SHA1;
import com.linksteady.qywx.domain.ExternalContact;
import com.linksteady.qywx.domain.SyncTask;
import com.linksteady.qywx.service.ApiService;
import com.linksteady.qywx.service.SyncTaskService;
import com.linksteady.qywx.service.UserMappingService;
import com.linksteady.qywx.service.WelcomeMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/syncTask")
@Slf4j
public class SyncTaskController extends ApiBaseController{
    private static final Object obj = new Object();
    private static final Object deptLock = new Object();
    private static final Object externalContactLock = new Object();

    @Autowired
    SyncTaskService syncTaskService;
    @Autowired
    ApiService apiService;

    private static final String SUBMIT_TASK_URL="/api/submitSyncTask";

    @Autowired
    UserMappingService userMappingService;

    @Autowired
    WelcomeMessageService welcomeMessageService;

    /**
     * 提交同步导购任务
     */
    @PostMapping("/submitSyncFollowUser")
    public ResponseBo submitSyncFollowUser() {
        //提交同步导购任务
        String corpId=apiService.getQywxCorpId();
        List<SyncTask> taskList= Lists.newArrayList();
        taskList.add(new SyncTask(corpId,"FOLLOW_USER","P",new Date(),"admin"));

        //1.保存到本地
        syncTaskService.saveSyncTask(taskList);
        //2.提交企业微信
        String result=submitTask(corpId,taskList);

        if(StringUtils.isNotEmpty(result)&&200==JSON.parseObject(result).getIntValue("code"))
        {
            return ResponseBo.ok();
        }else
        {
            log.error("submitSyncFollowUser失败，错误原因为{}",result);
            return ResponseBo.error();
        }
    }

    /**
     *提交同步外部客户任务
     */
    @PostMapping("/submitSyncExternalUser")
    public ResponseBo submitSyncExternalUser() {
        String corpId=apiService.getQywxCorpId();
        List<SyncTask> taskList= Lists.newArrayList();
        taskList.add(new SyncTask(corpId,"EXTERNAL_LIST","P",new Date(),"admin"));

        //1.保存到本地
        syncTaskService.saveSyncTask(taskList);
        //2.提交企业微信
        String result=submitTask(corpId,taskList);

        if(StringUtils.isNotEmpty(result)&&200==JSON.parseObject(result).getIntValue("code"))
        {
            return ResponseBo.ok();
        }else
        {
            log.error("submitSyncExternalUser失败，错误原因为{}",result);
            return ResponseBo.error();
        }
    }

    /**
     * 提交同步管理员任务
     */
    @PostMapping("/submitSyncAdminList")
    public ResponseBo submitSyncAdminList() {
        String corpId=apiService.getQywxCorpId();
        List<SyncTask> taskList= Lists.newArrayList();
        taskList.add(new SyncTask(corpId,"ADMIN_LIST","P",new Date(),"admin"));

        //1.保存到本地
        syncTaskService.saveSyncTask(taskList);
        //2.提交企业微信
        String result=submitTask(corpId,taskList);

        if(StringUtils.isNotEmpty(result)&&200==JSON.parseObject(result).getIntValue("code"))
        {
            return ResponseBo.ok();
        }else
        {
            log.error("submitSyncAdminList失败，错误原因为{}",result);
            return ResponseBo.error();
        }
    }

    /**
     * 同时提交 同步导购、同步外部客户 同步管理员 任务
     */
    @PostMapping("/submitAll")
    public ResponseBo submitAll() {
        String corpId=apiService.getQywxCorpId();
        List<SyncTask> taskList= Lists.newArrayList();
        taskList.add(new SyncTask(corpId,"FOLLOW_USER","P",new Date(),"admin"));
        taskList.add(new SyncTask(corpId,"EXTERNAL_LIST","P",new Date(),"admin"));
        taskList.add(new SyncTask(corpId,"ADMIN_LIST","P",new Date(),"admin"));

        //1.保存到本地
        syncTaskService.saveSyncTask(taskList);
        //2.提交企业微信
        String result=submitTask(corpId,taskList);

        if(StringUtils.isNotEmpty(result)&&200==JSON.parseObject(result).getIntValue("code"))
        {
            return ResponseBo.ok();
        }else
        {
            log.error("submitAll失败，错误原因为{}",result);
            return ResponseBo.error();
        }
    }


    /**
     * 提交异步任务
     * @param corpId
     * @param taskList
     * @return
     */
    private String submitTask(String corpId,List<SyncTask> taskList)
    {
        String timestamp=String.valueOf(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
        String data=JSON.toJSONString(taskList);
        String signature= SHA1.gen(timestamp,data);
        //2.提交到企业微信端
        StringBuffer url=new StringBuffer(apiService.getQywxDomainUrl()+SUBMIT_TASK_URL);
        url.append("?corpId="+corpId);
        url.append("&timestamp="+timestamp);
        url.append("&signature="+signature);
        return OkHttpUtil.postRequestByJson(url.toString(),data);
    }

    /**
     * 接收企业微信端发过来 导购信息
     */
    @PostMapping("/syncFollowUser")
    public ResponseBo syncFollowUser(HttpServletRequest request,
                                     @RequestParam(name = "timestamp") String timestamp,
                                     @RequestParam(name = "signature") String signature,
                                     @RequestParam(name = "corpId") String corpId,
                                     @RequestParam(name = "data") String data) {
        try{
            validateLegality(request,signature,timestamp,corpId,data);
            //写入本地信息
            List<String> followUserList=JSONObject.parseArray(data,String.class);
            synchronized (externalContactLock)
            {
                syncTaskService.saveFollowUser(corpId,followUserList);
                //更新标记
                syncTaskService.saveChangeFlagToN("follow_user");
            }
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("syncFollowUser失败，错误原因为{}",e);
            return ResponseBo.error(e.getMessage());
        }
    }

    /**
     * 接收企业微信端发送回来的 同步任务状态
     */
    @PostMapping("/updateSyncTaskStatus")
    public ResponseBo updateSyncTaskStatus(HttpServletRequest request,
                                           @RequestParam(name = "timestamp") String timestamp,
                                           @RequestParam(name = "signature") String signature,
                                           @RequestParam(name = "taskId") String taskId,
                                           @RequestParam(name = "status") String status
    ) {
        try {
            validateLegality(request,signature,timestamp,taskId,status);
            //更新本地的企业微信同步任务状态
            syncTaskService.updateSyncTask(taskId,status);
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("updateSyncTaskStatus失败，错误原因为{}",e);
            return ResponseBo.error(e.getMessage());
        }
    }

    /**
     * 接收企业微信端返回来的部门信息
     */
    @PostMapping("/syncDept")
    public ResponseBo syncDept(HttpServletRequest request,
                                     @RequestParam(name = "timestamp") String timestamp,
                                     @RequestParam(name = "signature") String signature,
                                     @RequestParam(name = "data") String data,
                                     @RequestParam(name = "corpId") String corpId) {
        try {
            validateLegality(request,signature,timestamp,data,corpId);

            JSONArray deptArray=JSONObject.parseArray(data);
            synchronized (deptLock)
            {
                //更新所有的部门为不可用
                syncTaskService.updateDeptDisabled();
                if(null!=deptArray&&deptArray.size()>0)
                {
                    JSONObject target=null;
                    for(int i=0;i<deptArray.size();i++)
                    {
                        target=deptArray.getJSONObject(i);
                        syncTaskService.saveDept(target.getLongValue("id"),target.getLongValue("parentId"),target.getString("name"),target.getIntValue("orderNo"));
                    }
                }
                syncTaskService.saveChangeFlagToN("party");
                return ResponseBo.ok();
            }
        } catch (Exception e) {
            log.error("syncFollowUser失败，错误原因为{}",e);
            return ResponseBo.error(e.getMessage());
        }
    }

    /**
     * 接收企业微信端返回来的 变更信息
     */
    @PostMapping("/syncChangeFlag")
    public ResponseBo syncChangeFlag(HttpServletRequest request,
                                     @RequestParam(name = "timestamp") String timestamp,
                                     @RequestParam(name = "signature") String signature,
                                     @RequestParam(name = "data") String data,
                                     @RequestParam(name = "corpId") String corpId) {
        try {
            validateLegality(request,signature,timestamp,corpId,data);
            String changeCode=data;
            synchronized (obj)
            {
                syncTaskService.saveChangeFlag(changeCode);
            }
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("同步更新状态失败，原因为{}",e);
            return ResponseBo.error(e.getMessage());
        }
    }


    /**
     * 接收企业微信端发过来的外部联系人信息
     * 备注：此方法一般只用于期初
     */
    @PostMapping("/syncExternalContract")
    public ResponseBo syncExternalContract(HttpServletRequest request,
                                     @RequestParam(name = "timestamp") String timestamp,
                                     @RequestParam(name = "signature") String signature,
                                     @RequestParam(name = "corpId") String corpId,
                                     @RequestParam(name = "data") String data) {
        try{
            validateLegality(request,signature,timestamp,corpId,data);
            //写入本地信息
            List<ExternalContact> externalContactList=JSONObject.parseArray(data, ExternalContact.class);
            synchronized (externalContactLock)
            {
                syncTaskService.saveExternalContactList(externalContactList);
                //更新标记
                syncTaskService.saveChangeFlagToN("follow_user");
                return ResponseBo.ok();
            }
        } catch (Exception e) {
            log.error("saveExternalContactList失败，错误原因为{}",e);
            return ResponseBo.error(e.getMessage());
        }
    }

    /**
     * 获取有效的欢迎语信息
     */
    @PostMapping("/getWelcomeMessage")
    public ResponseBo getWelcomeMessage(HttpServletRequest request,
                                           @RequestParam(name = "timestamp") String timestamp,
                                           @RequestParam(name = "signature") String signature,
                                           @RequestParam(name = "corpId") String corpId) {
        try{
            validateLegality(request,signature,timestamp,corpId);

            //获取有效的欢迎语信息
            QywxMessage qywxMessage=welcomeMessageService.getValidWelcomeMessage();
            if(null==qywxMessage)
            {
                return ResponseBo.error("无法找到有效的欢迎语配置!");
            }else
            {
                return ResponseBo.okWithData("",qywxMessage);
            }
        } catch (Exception e) {
            log.error("获取有效的欢迎语信息失败，错误原因为{}",e);
            return ResponseBo.error(e.getMessage());
        }
    }

    /**
     * 接收外部联系人删除事件
     * @param request
     * @param signature
     * @param timestamp
     * @param externalUserId
     * @param followUserId
     * @return
     */
    @PostMapping("/delExternalContact")
    public ResponseBo userMapping(HttpServletRequest request, String signature, String timestamp,
                                  String externalUserId,
                                  String followUserId,
                                  String corpId) {
        try {
            validateLegality(request,signature,timestamp,followUserId,externalUserId,corpId);
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }

        //对参数进行校验
        if(StringUtils.isEmpty(externalUserId)||StringUtils.isEmpty(followUserId))
        {
            return ResponseBo.error("参数不能为空!");
        }


        try {
            //对当前匹配到的用户进行清除
            userMappingService.deleteMappingInfo(externalUserId,followUserId);
            //清除本地的外部联系人
            syncTaskService.delExternalContact(externalUserId,followUserId,corpId);
            return ResponseBo.ok();
        } catch (Exception e) {
            log.error("删除用户匹配关系错误，错误原因为{}",e);
            return ResponseBo.error(e.getMessage());
        }

    }

    /**
     * 外部联系人新增/修改事件
     */
    /**
     * update
     * @param request
     * @param signature
     * @param timestamp
     * @param corpId 公司ID
     * @param data 外部联系人ID
     * @return
     */
    @PostMapping("/syncExternalContractSingle")
    public ResponseBo userMapping(HttpServletRequest request, String signature, String timestamp,
                                  String corpId,
                                  String data) {
        try {
            validateLegality(request,signature,timestamp,corpId,data);
        } catch (Exception e) {
            return ResponseBo.error(e.getMessage());
        }
        //对参数进行校验
        if(StringUtils.isEmpty(corpId)||StringUtils.isEmpty(data))
        {
            return ResponseBo.error("参数不能为空!");
        }
        Long userId=null;
        ExternalContact externalContact=JSON.parseObject(data,ExternalContact.class);
        //对外部联系人进行insert or update
        syncTaskService.saveExternalContact(externalContact);

        //todo 手机号存储在企业微信的备注字段上
        String phoneNum=externalContact.getDescription();

        if(StringUtils.isNotEmpty(phoneNum))
        {
            userId=userMappingService.updateMappingInfo(externalContact.getExternalUserid(),externalContact.getFollowerUserId(),phoneNum);
        }else
        {
            //将externalUserId对应的映射信息全部清空 以为将外部联系人标记的手机号置为空了 所有要将之前匹配上的信息清空
            userMappingService.deleteMappingInfo(externalContact.getExternalUserid(),externalContact.getFollowerUserId());
        }
        return ResponseBo.okWithData("",userId);
    }



}
