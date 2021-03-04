package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.FollowUserMapper;
import com.linksteady.qywx.dao.QywxDeptMapper;
import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.domain.WxError;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.FollowUserService;
import com.linksteady.qywx.service.QywxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
public class FollowUserServiceImpl implements FollowUserService {

    @Autowired
    QywxService qywxService;

    @Autowired
    FollowUserMapper followUserMapper;

    @Autowired
    QywxDeptMapper qywxDeptMapper;

    @Override
    public List<String> callFollowUserRemote() throws WxErrorException{
        StringBuffer requestUrl=new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_FOLLOW_USER_LIST));

        requestUrl.append("?access_token="+qywxService.getAccessToken());
        String followUserResult= OkHttpUtil.getRequest(requestUrl.toString());
        log.info("获取导购信息返回的结果为{}",followUserResult);
        JSONObject jsonObject = JSON.parseObject(followUserResult);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            log.info("获取导购信息错误，原因为{}",error);
            throw new WxErrorException(error);
        }
        JSONArray jsonArray=jsonObject.getJSONArray("follow_user");
        return jsonArray.toJavaList(String.class);
    }

    /**
     * 获取 成员的详细信息
     * @param followerUserId
     * @return
     */
    @Override
    public FollowUser callFollowUserDetailRemote(String followerUserId) throws WxErrorException{
        StringBuffer requestUrl=new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.User.USER_GET));

        requestUrl.append(followerUserId);
        requestUrl.append("&access_token="+qywxService.getAccessToken());

        String result=OkHttpUtil.getRequest(requestUrl.toString());
        JSONObject jsonObject =JSON.parseObject(result);
        log.info("获取导购详情信息，返回的结果为{}",jsonObject);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            log.info("获取导购详细信息失败，原因为{}",error);
            throw new WxErrorException(error);
        }

        FollowUser followUser=new FollowUser().buildFromJsonObject(jsonObject);
        return followUser;
    }

    /**
     * 同步导购信息
     * @param
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized void syncQywxFollowUser() throws Exception
    {
        log.info("同步具有外部联系权限的成员列表");
        //获取配置了客户联系功能的成员列表
        List<String> followerUserList =this.callFollowUserRemote();
        log.info("处理标志位");
        //更新删除标记位为1
        followUserMapper.updateDeleteFlag();
        //对成员列表进行保存(设置删除标志为0)
        if(followerUserList.size()>0)
        {
            followUserMapper.saveFollowerUser(followerUserList);
        }

        //删除标志为1的记录
        followUserMapper.deleteFollowUser();
        log.info("对详细信息进行更新");
        //对本地的导购详细信息进行更新
        FollowUser followUser=null;

        int count=0;
        for(String followerUserId:followerUserList)
        {
            count++;
            //查询成员的详细信息，并进行更新
            followUser = callFollowUserDetailRemote(followerUserId);
            log.info("{}对follower:{}的详细信息进行更新",count,followerUserId);
            followUserMapper.updateFollowUser(followUser);
        }
        log.info("同步具有外部联系权限的成员列表结束");
    }

    @Override
    public synchronized void syncDept() throws Exception {
        log.info("开始同步部门信息");
        StringBuffer requestUrl=new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.Department.DEPARTMENT_LIST));
        requestUrl.append("?access_token="+qywxService.getAccessToken());

        String deptResult= OkHttpUtil.getRequest(requestUrl.toString());
        JSONObject jsonObject = JSON.parseObject(deptResult);
        log.debug("获取部门列表接口返回的结果为{}",jsonObject);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
            throw new WxErrorException(error);
        }
        JSONArray deptArray=jsonObject.getJSONArray("department");

        //设置所有的部门是否可用为 N不可用
        followUserMapper.updateDeptDeleteFlag();
        if(null!=deptArray&&deptArray.size()>=0)
        {
            JSONObject temp=null;
            for(int i=0;i<deptArray.size();i++)
            {
                temp=deptArray.getJSONObject(i);
                //insert or update
                qywxDeptMapper.saveDept(temp.getLong("id"),temp.getString("name"),temp.getLong("parentid"),temp.getIntValue("order"));
            }
        }

        qywxDeptMapper.deleteDept();
        log.info("同步部门信息结束");
    }

    @Override
    public List<FollowUser> getFollowUserList() {
        return followUserMapper.getFollowUserList();
    }
}
