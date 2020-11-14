package com.linksteady.qywx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.linksteady.common.util.OkHttpUtil;
import com.linksteady.qywx.constant.WxPathConsts;
import com.linksteady.qywx.dao.FollowUserMapper;
import com.linksteady.qywx.domain.FollowUser;
import com.linksteady.qywx.domain.WxError;
import com.linksteady.qywx.exception.WxErrorException;
import com.linksteady.qywx.service.FollowUserService;
import com.linksteady.qywx.service.QywxService;
import com.linksteady.qywx.vo.FollowUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FollowUserServiceImpl implements FollowUserService {

    @Autowired
    QywxService qywxService;

    @Autowired
    FollowUserMapper followUserMapper;

    @Override
    public List<String> selectFollowUserList() throws WxErrorException{
        StringBuffer requestUrl=new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.ExternalContacts.GET_FOLLOW_USER_LIST));

        requestUrl.append("?access_token="+qywxService.getAccessToken());
        String followUserResult= OkHttpUtil.getRequest(requestUrl.toString());
        JSONObject jsonObject = JSON.parseObject(followUserResult);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
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
    public FollowUser selectUserDetail(String followerUserId) throws WxErrorException{
        StringBuffer requestUrl=new StringBuffer(qywxService.getRedisConfigStorage().getApiUrl(WxPathConsts.User.USER_GET));

        requestUrl.append(followerUserId);
        requestUrl.append("&access_token="+qywxService.getAccessToken());

        String result=OkHttpUtil.getRequest(requestUrl.toString());
        JSONObject jsonObject =JSON.parseObject(result);
        WxError error = WxError.fromJsonObject(jsonObject);
        if (error.getErrorCode() != 0) {
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
        //获取配置了客户联系功能的成员列表
        List<String> followerUserList =this.selectFollowUserList();
        //更新删除标记位为1
        followUserMapper.updateDeleteFlag();
        //对成员列表进行保存(设置删除标志为0)
        followUserMapper.saveFollowerUser(followerUserList);

        //删除标志为1的记录
        followUserMapper.deleteFollowUser();

        //对本地的导购详细信息进行更新
        FollowUser followUser=null;

        for(String followerUserId:followerUserList)
        {
            //查询成员的详细信息，并进行更新
            followUser = selectUserDetail(followerUserId);
            followUserMapper.updateFollowUser(followUser);
        }
    }
}
